package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.adapters.outbound.security.JwtTokenProvider;
import br.com.knowledgebase.domain.ports.in.LoginUseCase;
import br.com.knowledgebase.domain.ports.in.TwoFactorVerifyUseCase;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticação via usuário/senha e verificação 2FA (TOTP)")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final TwoFactorVerifyUseCase twoFactorVerifyUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    // Gerador seguro p/ refresh tokens (string aleatória url-safe)
    private static final SecureRandom RNG = new SecureRandom();

    public AuthController(LoginUseCase loginUseCase,
                          TwoFactorVerifyUseCase twoFactorVerifyUseCase,
                          JwtTokenProvider jwtTokenProvider) {
        this.loginUseCase = loginUseCase;
        this.twoFactorVerifyUseCase = twoFactorVerifyUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public record RevalidateRequest(String token) {}
    public record RevalidateResponse(String token) {}

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Realiza login",
            description = """
            Valida usuário/senha (BCrypt).
            • Se 2FA estiver habilitado e o TOTP NÃO for enviado, retorna status="2FA_REQUIRED".
            • Se TOTP for enviado e válido (ou se 2FA não estiver habilitado), retorna status="OK", o JWT (access token) e define um cookie HttpOnly com o refresh token.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        // 1) Valida usuário/senha
        LoginUseCase.Result res = loginUseCase.login(req.username(), req.password());

        // 2) Se não há 2FA, devolve access token e seta refresh cookie
        if (res.decision() == LoginUseCase.Decision.OK) {
            String accessToken = res.token(); // vindo do caso de uso
            return issueTokensAndSetCookie(res.username(), accessToken);
        }

        // 3) Há 2FA habilitado: se TOTP não veio, sinaliza necessidade
        if (req.totp() == null || req.totp().isBlank()) {
            return ResponseEntity.ok(new LoginResponse("2FA_REQUIRED", null));
        }

        // 4) TOTP informado: verifica e retorna access token + refresh cookie
        String accessToken = twoFactorVerifyUseCase.verify(req.username(), req.totp().trim());
        return issueTokensAndSetCookie(req.username(), accessToken);
    }

    @PostMapping(
            path = "/2fa/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Verifica TOTP",
            description = "Valida o código TOTP e retorna um JWT (access token) + define refresh token via cookie."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Código TOTP inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<LoginResponse> verify(@Valid @RequestBody TwoFactorVerifyRequest req) {
        String accessToken = twoFactorVerifyUseCase.verify(req.username(), req.code());
        return issueTokensAndSetCookie(req.username(), accessToken);
    }

    @PostMapping(
            path = "/revalidate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RevalidateResponse> revalidate(@RequestBody RevalidateRequest body) {

        String oldToken = (body == null ? null : body.token());
        if (oldToken == null || oldToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        final long windowMs = 60_000L;

        final Claims claims;
        try {
            // PONTO DO AJUSTE: não usar validate() aqui (ele bloqueia expirado).
            // Aqui precisamos apenas garantir assinatura/issuer e então aplicar a janela de 60s.
            claims = jwtTokenProvider.parseClaimsAllowExpired(oldToken);
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }

        long deltaMs = claims.getExpiration().getTime() - System.currentTimeMillis();

        // cedo demais (ainda falta > 60s)
        if (deltaMs > windowMs) {
            return ResponseEntity.noContent().build();
        }

        // tarde demais (expirou há mais de 60s) => força login
        if (deltaMs < -windowMs) {
            return ResponseEntity.status(401).build();
        }

        // dentro da janela: faltando <= 60s OU expirou há <= 60s => revalida
        String newToken = jwtTokenProvider.generate(claims.getSubject());
        return ResponseEntity.ok(new RevalidateResponse(newToken));
    }
    /*
    @PostMapping(
            path = "/revalidate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Revalida um token ainda válido e emite um novo (apenas nos últimos 60s antes de expirar)")
    public ResponseEntity<RevalidateResponse> revalidate(@RequestBody RevalidateRequest body) {

        // 1) Recupera e valida presença
        String oldToken = (body == null ? null : body.token());
        if (oldToken == null || oldToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        final long windowMs = 60_000L;
        // final long windowMs = Duration.ofSeconds(60).toMillis();

        // 2) Validação estrita: assinatura + issuer + expiração (ANTES de expirar)
        // FIX: agora é coerente com a expectativa "antes de expirar"
        if (!jwtTokenProvider.validate(oldToken)) {
            return ResponseEntity.status(401).build();
        }

        // 3) Calcula ms restantes com base no provider (coerente e normalizado)
        long msLeft = jwtTokenProvider.millisToExpire(oldToken);

        // Segurança defensiva: mesmo validado, pode ter expirado entre as chamadas
        if (msLeft <= 0) {
            return ResponseEntity.status(401).build();
        }

        // 4) Fora da janela final: não revalida
        if (msLeft > windowMs) {
            return ResponseEntity.noContent().build(); // 204
        }

        // 5) Dentro dos últimos 60s: emite novo token
        String username = jwtTokenProvider.subject(oldToken);
        String newToken = jwtTokenProvider.generate(username);

        return ResponseEntity.ok(new RevalidateResponse(newToken));
    }
    */
    /*
    public ResponseEntity<?> revalidate(@RequestBody Map<String, String> body) {
        // 1) Recupera o token do body
        String oldToken = body.get("token");
        if (oldToken == null || oldToken.isBlank()) {
            // requisição mal formada (sem token)
            return ResponseEntity.badRequest().build();
        }

        // 2) Normaliza: remove prefixo "Bearer " se vier assim do front/BFF
        oldToken = oldToken.trim();
        if (oldToken.toLowerCase().startsWith("bearer ")) {
            oldToken = oldToken.substring(7).trim();
        }

        // 3) Valida assinatura, formato e expiração
        if (!jwtTokenProvider.validate(oldToken)) {
            return ResponseEntity.status(401).build();
        }

        // 4) Verifica janela final de 60s
        long msLeft = jwtTokenProvider.millisToExpire(oldToken);
        if (msLeft <= 60_000 && msLeft > 0) {
            String username = jwtTokenProvider.subject(oldToken);
            String newToken = jwtTokenProvider.generate(username);
            // Mantém o refresh cookie como está; apenas troca o access token
            return ResponseEntity.ok(Map.of("token", newToken));
        }

        // Token ainda não está na janela de renovação: 204 (sem corpo)
        return ResponseEntity.noContent().build();
    }
    */

    // ===== Helpers =====

    /**
     * Emite/atualiza o refresh cookie (apenas emissão; validação/rotação ficam para um endpoint futuro)
     * e devolve o access token no corpo.
     */
    private ResponseEntity<LoginResponse> issueTokensAndSetCookie(String username, String accessToken) {
        String refresh = generateRefreshToken();

        // Em dev: secure(false), sameSite("Lax").
        // Em produção (HTTPS e possivelmente domínios distintos): secure(true), sameSite("None") e ajustar 'domain' se necessário.
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true)
                .secure(false)               // PRODUÇÃO: true
                .sameSite("Lax")             // Cross-site + HTTPS => "None"
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))  // ou parametrizar via properties
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse("OK", accessToken));
    }

    private static String generateRefreshToken() {
        byte[] buf = new byte[32];
        RNG.nextBytes(buf);
        // URL-safe, sem padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    // ---------------- DTOs ----------------

    @Schema(name = "LoginRequest", description = "Credenciais de login com TOTP opcional")
    public record LoginRequest(
            @NotBlank
            @Schema(description = "Usuário (login)", example = "alexandre")
            String username,

            @NotBlank
            @Schema(description = "Senha do usuário (BCrypt no backend)", example = "P@ssw0rd!")
            String password,

            @Schema(description = "Código TOTP de 6 dígitos, quando 2FA estiver habilitado", example = "123456")
            @Pattern(regexp = "^\\d{6}$", message = "TOTP deve ter 6 dígitos")
            String totp
    ) {}

    @Schema(name = "TwoFactorVerifyRequest", description = "Verificação de TOTP (2FA)")
    public record TwoFactorVerifyRequest(
            @NotBlank
            @Schema(description = "Usuário (login)", example = "alexandre")
            String username,

            @NotBlank
            @Pattern(regexp = "^\\d{6}$", message = "TOTP deve ter 6 dígitos")
            @Schema(description = "Código TOTP de 6 dígitos", example = "123456")
            String code
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "LoginResponse", description = "Resposta de login/verificação 2FA")
    public record LoginResponse(
            @Schema(description = "Status da autenticação", example = "OK", allowableValues = {"OK", "2FA_REQUIRED"})
            String status,

            @Schema(description = "JWT Bearer quando status='OK'", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String token
    ) {}
}
