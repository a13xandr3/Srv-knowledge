package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.domain.ports.in.TwoFactorVerifyUseCase;
import br.com.knowledgebase.domain.ports.out.TokenProviderPort;
import br.com.knowledgebase.domain.ports.out.TwoFactorServicePort;
import br.com.knowledgebase.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorVerifyUseCaseImpl implements TwoFactorVerifyUseCase {
    private final UserRepositoryPort userRepo;
    private final TwoFactorServicePort totpService;
    private final TokenProviderPort tokenProvider;

    public TwoFactorVerifyUseCaseImpl(UserRepositoryPort userRepo, TwoFactorServicePort totpService,
                                      TokenProviderPort tokenProvider
                                     ) {
        this.userRepo = userRepo;
        this.totpService = totpService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String verify(String username, String totpCode) {

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        String secret = user.getTwoFaSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("2FA não habilitado para o usuário.");
        }

        String code = totpCode == null ? "" : totpCode.trim();
        if (!code.matches("\\d{6}")) {
            throw new IllegalArgumentException("Código 2FA inválido");
        }

        if (!totpService.isValidCode(secret, code)) {
            throw new IllegalArgumentException("Código 2FA inválido");
        }

        // Somente aqui emitimos o JWT (usuário+senha já validados antes no LoginUseCase)
        return tokenProvider.generate(user.getUsername());
    }
}
