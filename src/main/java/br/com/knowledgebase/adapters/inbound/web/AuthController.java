package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.domain.ports.in.LoginUseCase;
import br.com.knowledgebase.domain.ports.in.TwoFactorVerifyUseCase;

import com.fasterxml.jackson.annotation.JsonInclude;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticação via usuário/senha e verificação 2FA (TOTP)")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final TwoFactorVerifyUseCase twoFactorVerifyUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          TwoFactorVerifyUseCase twoFactorVerifyUseCase) {
        this.loginUseCase = loginUseCase;
        this.twoFactorVerifyUseCase = twoFactorVerifyUseCase;
    }

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
            • Se TOTP for enviado e válido (ou se 2FA não estiver habilitado), retorna status="OK" e o token JWT.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest req
    ) {
        // 1) Valida usuário/senha
        LoginUseCase.Result res = loginUseCase.login(req.username(), req.password());

        // 2) Se não há 2FA, devolve o JWT direto
        if (res.decision() == LoginUseCase.Decision.OK) {
            return ResponseEntity.ok(new LoginResponse("OK", res.token()));
        }

        // 3) Há 2FA habilitado: se TOTP não veio, sinaliza necessidade
        if (req.totp() == null || req.totp().isBlank()) {
            return ResponseEntity.ok(new LoginResponse("2FA_REQUIRED", null));
        }

        // 4) TOTP informado: verifica e retorna JWT em caso de sucesso
        String jwt = twoFactorVerifyUseCase.verify(req.username(), req.totp().trim());
        return ResponseEntity.ok(new LoginResponse("OK", jwt));
    }

    @PostMapping(
            path = "/2fa/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Verifica TOTP",
            description = "Valida o código TOTP e retorna um JWT em caso de sucesso."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Código TOTP inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<LoginResponse> verify(@Valid @RequestBody TwoFactorVerifyRequest req) {
        String jwt = twoFactorVerifyUseCase.verify(req.username(), req.code());
        return ResponseEntity.ok(new LoginResponse("OK", jwt));
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
