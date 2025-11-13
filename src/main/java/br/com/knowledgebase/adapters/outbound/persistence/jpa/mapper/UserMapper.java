package br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.UserJpaEntity;
import br.com.knowledgebase.domain.model.User;

public final class UserMapper {
    private UserMapper() {}

    public static User toDomain(UserJpaEntity e) {
        if (e == null) return null;
        var d = new User();
        d.setId(e.getId());
        d.setUsername(e.getUsername());
        d.setPasswordHash(e.getPasswordHash());
        d.setTwoFaEnabled(Boolean.TRUE.equals(e.getTwoFaEnabled()));
        d.setTwoFaSecret(e.getTwoFaSecret());
        return d;
    }

    public static UserJpaEntity toJpa(User d) {
        if (d == null) return null;
        var e = new UserJpaEntity();
        e.setId(d.getId());
        e.setUsername(d.getUsername());
        e.setPasswordHash(d.getPasswordHash());
        e.setTwoFaEnabled(d.isTwoFaEnabled());
        e.setTwoFaSecret(d.getTwoFaSecret());
        return e;
    }
}
