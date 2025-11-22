package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.domain.ports.in.LoginUseCase;
import br.com.knowledgebase.domain.ports.out.TokenProviderPort;
import br.com.knowledgebase.domain.ports.out.UserRepositoryPort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepositoryPort userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginUseCaseImpl(UserRepositoryPort userRepo,
                            PasswordEncoder passwordEncoder,
                            TokenProviderPort tokenProvider) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Result login(String username, String password) {

        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        final String raw = password == null ? "" : password;
        final String stored = user.getPasswordHash() == null ? "" : user.getPasswordHash().trim();

        // sanity check do formato BCrypt para evitar mensagens confusas
        if (stored.length() < 60 || !(stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            throw new IllegalStateException("Hash BCrypt inválido/ausente para o usuário: " + user.getUsername());
        }

        if (!passwordEncoder.matches(raw, stored)) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        if (user.isTwoFaEnabled()) {
            // NÃO envie o TOTP aqui — apenas sinalize que precisa do segundo fator
            return new Result(Decision.TWO_FA_REQUIRED, null);
        }

        String jwt = tokenProvider.generate(user.getUsername());
        return new Result(Decision.OK, jwt);
    }
}
