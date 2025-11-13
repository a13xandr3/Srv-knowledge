package br.com.knowledgebase;

import br.com.knowledgebase.config.CryptoRsaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CryptoRsaProperties.class)
public class KnowledgedbaseApplication {
    public static void main(String[] args) {

        SpringApplication.run(KnowledgedbaseApplication.class, args);

    }
}
