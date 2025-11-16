package br.com.knowledgebase.application.usecases;

import org.springframework.stereotype.Service;

import br.com.knowledgebase.domain.ports.in.TwoFactorSetupUseCase;
import br.com.knowledgebase.domain.ports.out.TotpProvisioningPort;
import br.com.knowledgebase.domain.ports.out.UserRepositoryPort;

import java.util.NoSuchElementException;

@Service
public class TwoFactorSetupUseCaseImpl implements TwoFactorSetupUseCase {
    private final UserRepositoryPort userRepo;
    private final TotpProvisioningPort totp;

    public TwoFactorSetupUseCaseImpl(UserRepositoryPort userRepo, TotpProvisioningPort totp) { this.userRepo = userRepo; this.totp = totp; }

    @Override
    public String setup(String username) {
        userRepo.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
        String secret = totp.generateSecret();
        userRepo.updateTwoFaSecret(username, secret);
        userRepo.enableTwoFa(username, true);
        return totp.otpauthUri("KnowledgeApp", username, secret);
    }
}
