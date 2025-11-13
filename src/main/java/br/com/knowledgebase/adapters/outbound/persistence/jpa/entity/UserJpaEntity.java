package br.com.knowledgebase.adapters.outbound.persistence.jpa.entity;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "usr", indexes = {
        @Index(name="ux_usr_username", columnList = "username", unique = true)
})
public class UserJpaEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name="username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name="passwordHash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name="twoFaSecret", length = 255)
    private String twoFaSecret;

    @Column(name="twoFaEnabled", nullable = false)
    private Boolean twoFaEnabled = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTwoFaSecret() {
        return twoFaSecret;
    }

    public void setTwoFaSecret(String twoFaSecret) {
        this.twoFaSecret = twoFaSecret;
    }

    public Boolean getTwoFaEnabled() {
        return twoFaEnabled;
    }

    public void setTwoFaEnabled(Boolean twoFaEnabled) {
        this.twoFaEnabled = twoFaEnabled;
    }

}