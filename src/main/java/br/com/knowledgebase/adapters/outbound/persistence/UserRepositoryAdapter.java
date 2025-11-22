package br.com.knowledgebase.adapters.outbound.persistence;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.JpaUserRepository;
import br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper.UserMapper;
import br.com.knowledgebase.domain.model.User;
import br.com.knowledgebase.domain.ports.out.UserRepositoryPort;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpa;

    public UserRepositoryAdapter(JpaUserRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpa.findByUsername(username).map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = UserMapper.toJpa(user);
        var saved  = jpa.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void updateTwoFaSecret(String username, String secret) {
        jpa.updateTwoFaSecret(username, secret);
    }

    @Override
    public Optional<String> getTwoFaSecret(String username) {
        return jpa.findTwoFaSecret(username);
    }

    @Override
    @Transactional
    public void enableTwoFa(String username, boolean enabled) {
        jpa.updateTwoFaEnabled(username, enabled);
    }
}
