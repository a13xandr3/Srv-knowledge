package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.adapters.inbound.web.dto.TwoFactorSetupRequest;
import br.com.knowledgebase.adapters.inbound.web.dto.TwoFactorVerifyRequest;
import br.com.knowledgebase.domain.ports.in.TwoFactorSetupUseCase;
import br.com.knowledgebase.domain.ports.in.TwoFactorVerifyUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    private final TwoFactorSetupUseCase setupUseCase;
    private final TwoFactorVerifyUseCase verifyUseCase;

    public TwoFactorController(TwoFactorSetupUseCase setupUseCase, TwoFactorVerifyUseCase verifyUseCase) {
        this.setupUseCase = setupUseCase;
        this.verifyUseCase = verifyUseCase;
    }

    @Tag(name = "2FA")
    @PostMapping(value = "/setup", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cria segredo TOTP e retorna otpauth URI")
    public ResponseEntity<?> setup(@jakarta.validation.Valid @RequestBody TwoFactorSetupRequest body) {

        String username = body.username();

        String otpAuth = setupUseCase.setup(username);

        return ResponseEntity.ok(Map.of("otpAuth", otpAuth));
    }

    @PostMapping(value = "/verify", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Valida TOTP e emite JWT")
    public ResponseEntity<?> verify(@jakarta.validation.Valid @RequestBody TwoFactorVerifyRequest body) {
        String username = body.username();
        String code = body.code();
        String jwt = verifyUseCase.verify(username, code);
        return ResponseEntity.ok(Map.of("token", jwt));
    }
}
