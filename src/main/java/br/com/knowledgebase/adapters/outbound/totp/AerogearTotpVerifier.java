package br.com.knowledgebase.adapters.outbound.totp;

import br.com.knowledgebase.domain.ports.out.TotpVerifierPort;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Component;

@Component
public class AerogearTotpVerifier implements TotpVerifierPort {

    @Override
    public boolean isValid(String base32Secret, String code) {
        if (base32Secret == null || base32Secret.isBlank()) return false;
        if (code == null || code.isBlank()) return false;
        try {
            // Verifica TOTP (6 dígitos, janela padrão de 30s)
            return new Totp(base32Secret).verify(code);
        } catch (Exception e) {
            // Segura: qualquer problema retorna false
            return false;
        }
    }
}
