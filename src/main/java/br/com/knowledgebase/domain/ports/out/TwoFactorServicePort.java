package br.com.knowledgebase.domain.ports.out;

public interface TwoFactorServicePort {
    /**
     * Valida um código TOTP (6 dígitos) para um secret Base32.
     */
    boolean isValidCode(String secretBase32, String code6);
}