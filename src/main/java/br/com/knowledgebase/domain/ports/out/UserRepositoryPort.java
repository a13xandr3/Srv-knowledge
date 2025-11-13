package br.com.knowledgebase.domain.ports.out;

import br.com.knowledgebase.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    User save(User user);
    void updateTwoFaSecret(String username, String secret);
    Optional<String> getTwoFaSecret(String username);
    void enableTwoFa(String username, boolean enabled);
}