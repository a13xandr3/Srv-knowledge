package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.domain.ports.in.LoginUseCase;
import br.com.knowledgebase.domain.ports.in.TwoFactorVerifyUseCase;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final TwoFactorVerifyUseCase twoFactorVerifyUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          TwoFactorVerifyUseCase twoFactorVerifyUseCase) {
        this.loginUseCase = loginUseCase;
        this.twoFactorVerifyUseCase = twoFactorVerifyUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza login", description = "Valida usuário/senha (BCrypt). Se 2FA estiver habilitado, retorna '2FA_REQUIRED'. Se TOTP for enviado e válido, retorna JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
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

    @PostMapping("/2fa/verify")
    @Operation(summary = "Verifica TOTP", description = "Valida o código TOTP e retorna um JWT em caso de sucesso.")
    public ResponseEntity<LoginResponse> verify(@Valid @RequestBody TwoFactorVerifyRequest req) {
        String jwt = twoFactorVerifyUseCase.verify(req.username(), req.code());
        return ResponseEntity.ok(new LoginResponse("OK", jwt));
    }

    // DTOs locais ao controller
    public record LoginRequest(String username, String password, String totp) {}

    public record TwoFactorVerifyRequest(String username, String code) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LoginResponse(String status, String token) {}
}
