package br.com.knowledgebase.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev") // só roda quando o perfil 'dev' estiver ativo
public class OneShotHashPrinter implements CommandLineRunner {

    private final PasswordEncoder encoder;

    public OneShotHashPrinter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        String h = encoder.encode("S@ir@m10");
        System.out.println("[DEBUG] BCrypt hash p/ S@ir@m10 = " + h);
    }
}
