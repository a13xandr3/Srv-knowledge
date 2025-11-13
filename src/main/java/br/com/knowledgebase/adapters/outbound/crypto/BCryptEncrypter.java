package br.com.knowledgebase.adapters.outbound.crypto;

import br.com.knowledgebase.domain.ports.out.EncrypterPort;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptEncrypter implements EncrypterPort {

    @Override
    public String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(12));
    }
    @Override
    public boolean matches(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}