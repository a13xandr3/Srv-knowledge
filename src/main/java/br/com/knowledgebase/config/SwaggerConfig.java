package br.com.knowledgebase.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "SRV KnowledgeBase API Gateway",
        version = "1.0.0",
        description = "Srv responsável pela Autenticação, 2FA, Arquivos e Atividades"
))
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info() // <- use a classe de MODELO
                        .title("SRV KnowledgeBase API Gateway")
                        .version("1.0.0")
                        .description("SRV responsável pela Autenticação, 2FA, Arquivos e Atividades"));
    }
}