package br.com.knowledgebase.adapters.inbound.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthKeysController {

    private final String publicKeyPem;

    public AuthKeysController(
            @Value("${security.password-crypto.public-key-pem}") String publicKeyPem
    ) {
        this.publicKeyPem = publicKeyPem;
    }

    @GetMapping("/keys/password-encryption/public")
    public ResponseEntity<String> publicKey() {
        return ResponseEntity.ok(publicKeyPem);
    }

}
