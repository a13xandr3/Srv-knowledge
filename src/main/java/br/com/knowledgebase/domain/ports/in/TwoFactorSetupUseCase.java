package br.com.knowledgebase.domain.ports.in;

public interface TwoFactorSetupUseCase {
    String setup(String username);
}