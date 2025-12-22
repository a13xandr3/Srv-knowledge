package br.com.knowledgebase.adapters.outbound.security;

import br.com.knowledgebase.domain.ports.out.TokenProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    private final JwtProps props;
    private final Clock clock;
    private SecretKey key;

    public JwtTokenProvider(JwtProps props, Clock clock) {
        this.props = props;
        this.clock = clock;
    }

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private String issuer() {
        // FIX: garante coerência entre emissão e validação
        String iss = props.getIssuer();
        return (iss == null || iss.isBlank()) ? "knowledgebase" : iss;
    }

    /** Implementação oficial do Port (subject = username). */
    @Override
    public String generate(String subject) {
        // FIX: usar clock injetado (testável e consistente)
        Instant now = Instant.now(clock);
        Instant exp = now.plusSeconds(props.getExpirationSeconds());

        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer())                 // FIX: issuer único
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    /** Conveniência para manter compatibilidade com chamadores legados. */
    public String generateToken(String username) {
        return generate(username);
    }

    @Override
    public String subject(String token) {
        // FIX: normaliza e exige issuer (mesmo comportamento do validate)
        return parseClaimsStrict(token).getSubject();
    }

    @Override
    public boolean validate(String token) {
        String raw = normalize(token);
        if (raw == null || raw.isBlank()) return false;

        try {
            // FIX: valida assinatura + issuer + expiração (estrito)
            parseClaimsStrict(raw);
            return true;
        } catch (ExpiredJwtException e) {
            return false; // expirado => inválido para uso normal
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Conveniência para manter compatibilidade com chamadores legados. */
    public boolean validateToken(String token) {
        return validate(token);
    }

    /**
     * Milissegundos restantes (pode ser negativo se já expirou).
     * Útil para lógica de revalidação por "janela final".
     */
    public long millisToExpire(String token) {
        // FIX: normaliza + usa clock + não explode se expirado
        Claims claims = parseClaimsAllowExpired(token);
        Date exp = claims.getExpiration();
        return exp.getTime() - clock.millis();
    }

    // ---- parsing helpers ----

    /**
     * Parsing estrito: exige assinatura + issuer + exp (lança ExpiredJwtException se expirado).
     * Aceita token já normalizado (ou não; ainda assim normaliza).
     */
    private Claims parseClaimsStrict(String token) {
        String raw = normalize(token);
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Token vazio");
        }

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer())            // FIX: issuer coerente em todo parsing
                .build()
                .parseClaimsJws(raw)
                .getBody();
    }

    /**
     * Parsing tolerante a expiração: valida assinatura + issuer, e retorna claims mesmo expirado.
     * Uso típico: cálculo de ms restantes, auditoria, lógica de janela.
     */
    public Claims parseClaimsAllowExpired(String token) {
        String raw = normalize(token);
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Token vazio");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(issuer())        // FIX: issuer coerente
                    .build()
                    .parseClaimsJws(raw)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // FIX: assinatura/issuer já foram validados pelo parser; reaproveita claims
            return e.getClaims();
        }
    }

    private String normalize(String token) {
        if (token == null) return null;
        String raw = token.trim();
        if (raw.regionMatches(true, 0, "bearer ", 0, 7)) {
            raw = raw.substring(7).trim();
        }
        return raw;
    }
}
