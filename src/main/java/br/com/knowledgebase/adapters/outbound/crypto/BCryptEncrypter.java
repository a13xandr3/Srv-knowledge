package br.com.knowledgebase.adapters.outbound.crypto;

import br.com.knowledgebase.domain.ports.out.EncrypterPort;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    /*
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override
    public String hash(String raw) {
        /// return BCrypt.hashpw(raw, BCrypt.gensalt(12));
        return encoder.encode(raw);
    }

    //@Override
    //public boolean matches(String raw, String hashed) {
    //    return encoder.matches(raw, hashed);
        /// return BCrypt.checkpw(raw, hashed);
    //}

    @Override
    public boolean matches(String raw, String encoded) {
       if (encoded == null) return false;
       String e = encoded.trim();
       // se veio do DelegatingPasswordEncoder
               if (e.startsWith("{bcrypt}")) {
                e = e.substring("{bcrypt}".length());
            }
        // jBCrypt antigo não entende $2y$: normaliza para $2a$
                if (e.startsWith("$2y$")) {
                e = "$2a$" + e.substring(4);
            }
        return BCrypt.checkpw(raw, e);
    }
    */
}