package br.com.knowledgebase.config;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    private static final String[] SWAGGER = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    @Order(0)
    SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(SWAGGER)
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .exceptionHandling(eh -> eh.authenticationEntryPoint((req, res, ex) -> {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }));
        return http.build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Swagger e erro continuam livres
                    .requestMatchers(SWAGGER).permitAll()
                    .requestMatchers("/error").permitAll()

                    // Libera autenticação e 2FA
                    .requestMatchers("/api/auth/**").permitAll()

                    // Protege endpoints de domínio
                    .requestMatchers("/api/atividade/**").authenticated()
                    .requestMatchers("/api/files/**").authenticated()

                    // Qualquer outra coisa é bloqueada explicitamente
                    .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt())
            .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }));
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
