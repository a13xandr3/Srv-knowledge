package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.domain.ports.in.LoginUseCase;
import br.com.knowledgebase.domain.ports.out.TokenProviderPort;
import br.com.knowledgebase.domain.ports.out.UserRepositoryPort;
import br.com.knowledgebase.domain.model.User;
import org.springframework.security.authentication.BadCredentialsException;
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
        // 1) busca usuário pelo port EXISTENTE
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // 2) valida senha (raw x encoded)
        String encoded = user.getPasswordHash(); // seu modelo expõe passwordHash
        if (!passwordEncoder.matches(password, encoded)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // 3) se 2FA estiver habilitado, apenas sinaliza necessidade do TOTP
        if (user.isTwoFaEnabled()) {
            return new Result(Decision.TWO_FA_REQUIRED, null, user.getUsername());
        }

        // 4) gera access token pelo PORT (hexagonal)
        String subject = user.getUsername(); // username canônico
        String jwt = tokenProvider.generate(subject);

        return new Result(Decision.OK, jwt, subject);
    }
}