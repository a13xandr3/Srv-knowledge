package br.com.knowledgebase.adapters.outbound.persistence.jpa;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUsername(String username);

    @Query("select u.twoFaSecret from UserJpaEntity u where u.username = :username")
    Optional<String> findTwoFaSecret(@Param("username") String username);

    @Modifying
    @Query("update UserJpaEntity u set u.twoFaSecret = :secret where u.username = :username")
    int updateTwoFaSecret(@Param("username") String username, @Param("secret") String secret);

    @Modifying
    @Query("update UserJpaEntity u set u.twoFaEnabled = :enabled where u.username = :username")
    int updateTwoFaEnabled(@Param("username") String username, @Param("enabled") boolean enabled);
}
