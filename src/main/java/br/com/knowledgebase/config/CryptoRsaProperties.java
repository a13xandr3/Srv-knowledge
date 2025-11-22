package br.com.knowledgebase.config;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "crypto.rsa")
public class CryptoRsaProperties {

    /**
     * Habilita/Desabilita o uso de criptografia RSA.
     */
    private boolean enabled = true;

    /**
     * Hash usado no OAEP (ex: SHA-256).
     */
    @NotBlank
    private String oaepHash;

    /**
     * Caminho do arquivo PEM privado (opcional, usado quando os PEMs são externos).
     */
    private String privateKeyPath;

    /**
     * Caminho do arquivo PEM público (opcional, usado quando os PEMs são externos).
     */
    private String publicKeyPath;

    /**
     * PEM da chave privada (inline no YAML).
     */
    private String privateKeyPem;

    /**
     * PEM da chave pública (inline no YAML).
     */
    private String publicKeyPem;

    // Getters e setters
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getOaepHash() {
        return oaepHash;
    }
    public void setOaepHash(String oaepHash) {
        this.oaepHash = oaepHash;
    }

    public String getPrivateKeyPem() {
        return privateKeyPem;
    }
    public void setPrivateKeyPem(String privateKeyPem) {
        this.privateKeyPem = privateKeyPem;
    }

    public String getPublicKeyPem() {
        return publicKeyPem;
    }
    public void setPublicKeyPem(String publicKeyPem) {
        this.publicKeyPem = publicKeyPem;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }
    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }
    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }
}
