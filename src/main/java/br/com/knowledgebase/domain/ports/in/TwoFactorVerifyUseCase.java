package br.com.knowledgebase.domain.ports.in;

public interface TwoFactorVerifyUseCase {
    /**
     * Verifica o código TOTP do usuário e, se válido, retorna um JWT.
     * Lança IllegalArgumentException para código inválido/usuário inexistente.
     */
    String verify(String username, String totpCode);
}
