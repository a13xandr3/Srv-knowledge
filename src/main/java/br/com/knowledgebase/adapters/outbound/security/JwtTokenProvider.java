package br.com.knowledgebase.adapters.outbound.security;

import br.com.knowledgebase.domain.ports.out.TokenProviderPort;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    private final JwtProps props;
    private SecretKey key;

    public JwtTokenProvider(JwtProps props) {
        this.props = props;
    }

    @PostConstruct
    void init() {
        // Usa diretamente o segredo em texto simples configurado em security.jwt.secret
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generate(String subject) {

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getExpirationSeconds());

        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    @Override
    public String subject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}