package br.com.knowledgebase.adapters.outbound.totp;

import br.com.knowledgebase.domain.ports.out.TwoFactorServicePort;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Component
public class TwoFactorServiceImpl implements TwoFactorServicePort {

    @Override
    public boolean isValidCode(String secretBase32, String code6) {
        try {
            // código TOTP precisa ter exatamente 6 dígitos
            if (code6 == null || !code6.matches("\\d{6}")) return false;

            // normaliza e decodifica a chave base32 (como salva no banco)
            byte[] keyBytes = new Base32().decode(
                    secretBase32.replace(" ", "").toUpperCase(Locale.ROOT)
            );

            // TOTP padrão: 30s, 6 dígitos, HmacSHA1
            TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

            // >>> chave HMAC que o TOTP espera (NÃO crie Mac aqui)
            Key key = new SecretKeySpec(keyBytes, totp.getAlgorithm());

            // tolerância de relógio: janela de [-1, 0, +1] passos
            Instant now = Instant.now();
            Duration step = totp.getTimeStep();

            for (int i = -1; i <= 1; i++) {
                int expected = totp.generateOneTimePassword(key, now.plus(step.multipliedBy(i)));
                if (String.format("%06d", expected).equals(code6)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /*
    @Override
    public boolean isValidCode(String secretBase32, String code6) {
        try {
            var base32 = new Base32();
            byte[] key = base32.decode(secretBase32);

            var totp = new TimeBasedOneTimePasswordGenerator(); // padrão 30s, 6 dígitos, HMAC-SHA1
            var mac = Mac.getInstance(totp.getAlgorithm());
            mac.init(new SecretKeySpec(key, totp.getAlgorithm()));
            int expected = totp.generateOneTimePassword(mac, Instant.now());

            return String.format("%06d", expected).equals(code6);
        } catch (Exception e) {
            return false;
        }
    }
     */
}
