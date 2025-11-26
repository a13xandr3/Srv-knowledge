package br.com.knowledgebase.adapters.outbound.security;

import br.com.knowledgebase.domain.ports.out.TokenProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    private final JwtProps props;
    private SecretKey key;

    public JwtTokenProvider(JwtProps props) {
        this.props = props;
    }

    @PostConstruct
    void init() {
        // chave HMAC a partir do segredo configurado
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** Implementação oficial do Port (subject = username). */
    @Override
    public String generate(String subject) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getExpirationSeconds());
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key) // HS256 por padrão
                .compact();
    }

    /** Conveniência para manter compatibilidade com chamadores legados. */
    public String generateToken(String username) {
        return generate(username);
    }

    @Override
    public String subject(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public boolean validate(String token) {
        try {
            // valida assinatura, expiração e formato
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Conveniência para manter compatibilidade com chamadores legados. */
    public boolean validateToken(String token) {
        return validate(token);
    }

    /** Milissegundos restantes (pode ser negativo se já expirou). */
    public long millisToExpire(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp.getTime() - System.currentTimeMillis();
    }

    // ---- helpers ----
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}