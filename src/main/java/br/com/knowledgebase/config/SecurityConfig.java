package br.com.knowledgebase.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] SWAGGER = {
            "/swagger-ui.html", "/swagger-ui/**",
            "/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml",
            "/swagger-resources/**", "/webjars/**",
            "/error", "/favicon.ico" // evita redirect 403->/error bloquear
    };

    // Chain exclusiva do Swagger (SEM JWT) — sempre liberada
    @Bean @Order(1)
    SecurityFilterChain swaggerChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(SWAGGER)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.disable()); // importantíssimo
        return http.build();
    }

    // Chain da API — com JWT se houver JwtDecoder; sem ele, abre em dev
    @Bean @Order(2)
    SecurityFilterChain apiChain(HttpSecurity http, ObjectProvider<JwtDecoder> jwtDecoderProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        var decoder = jwtDecoderProvider.getIfAvailable();
        if (decoder != null) {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/actuator/health").permitAll()
                            .anyRequest().authenticated()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(decoder)));
        } else {
            // Sem JwtDecoder (dev): não quebre a app
            http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .oauth2ResourceServer(oauth2 -> oauth2.disable());
        }
        return http.build();
    }
}
