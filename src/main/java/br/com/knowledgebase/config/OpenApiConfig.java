package br.com.knowledgebase.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "SRV KnowledgeBase API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    GroupedOpenApi filesGroup() {
        return GroupedOpenApi.builder()
                .group("files")
                .pathsToMatch("/api/files/**")
                .build();
    }

    @Bean
    GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    GroupedOpenApi twoFaGroup() {
        return GroupedOpenApi.builder()
                .group("2fa")
                .pathsToMatch("/api/2fa/**")
                .build();
    }

    @Bean
    GroupedOpenApi activitiesGroup() {
        return GroupedOpenApi.builder()
                .group("activities")
                .pathsToMatch("/api/atividades/**")
                .build();
    }
}
