package br.com.knowledgebase.adapters.outbound.security;

import br.com.knowledgebase.domain.ports.out.TokenProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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

    /*
    @PostConstruct
    void init() {
        if (props.getSecret() == null || props.getSecret().isBlank()) {
            throw new IllegalStateException("JWT secret não definido (security.jwt.secret)");
        }
        byte[] keyBytes = props.isBase64()
                ? Decoders.BASE64.decode(props.getSecret())
                : props.getSecret().getBytes(StandardCharsets.UTF_8);

        // HS256 exige >= 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret deve ter pelo menos 32 bytes (256 bits). Tam atual: " + keyBytes.length);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    */

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