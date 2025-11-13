package br.com.knowledgebase.crypto;

import br.com.knowledgebase.config.CryptoRsaProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RsaOaepPasswordDecryptor {

    private static final Logger log = LoggerFactory.getLogger(RsaOaepPasswordDecryptor.class);

    private final CryptoRsaProperties props;

    private PrivateKey privateKey;
    private Cipher cipher;

    public RsaOaepPasswordDecryptor(CryptoRsaProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey();

            // OAEP robusto: usa o mesmo hash no digest e no MGF1
            final String hash = Objects.requireNonNull(props.getOaepHash(), "oaepHash"); // ex.: "SHA-256"
            this.cipher = Cipher.getInstance("RSA/ECB/OAEPWith" + hash.replace("-", "") + "AndMGF1Padding");

            OAEPParameterSpec spec = new OAEPParameterSpec(
                    hash,                         // digest (SHA-1, SHA-256, ...)
                    "MGF1",
                    new MGF1ParameterSpec(hash),  // MGF1 com o MESMO hash
                    PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.DECRYPT_MODE, privateKey, spec);

            log.info("[RSA] RsaOaepPasswordDecryptor inicializado (hash={}, keySource={}, pemOK)",
                    hash, props.getPrivateKeyPath() != null ? "FILE" : "INLINE");
        } catch (Exception e) {
            log.error("[RSA] Falha ao inicializar RsaOaepPasswordDecryptor: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha ao inicializar RsaOaepPasswordDecryptor: " + e.getMessage(), e);
        }
    }

    /**
     * Lê o PEM da chave privada (de arquivo ou inline) e retorna PrivateKey (PKCS#8).
     * Tolerante a quebras de linha/espacos no corpo Base64 (MIME).
     */
    private PrivateKey loadPrivateKey() throws Exception {
        // Escolhe a fonte: caminho do arquivo ou PEM inline
        String pem;
        if (props.getPrivateKeyPath() != null && !props.getPrivateKeyPath().isBlank()) {
            pem = Files.readString(Path.of(props.getPrivateKeyPath()), StandardCharsets.US_ASCII);
        } else if (props.getPrivateKeyPem() != null && !props.getPrivateKeyPem().isBlank()) {
            pem = props.getPrivateKeyPem();
        } else {
            throw new IllegalStateException("Nenhuma chave privada configurada (private-key-path ou private-key-pem).");
        }

        // Extrai SOMENTE o miolo Base64 entre os delimitadores PEM (RFC 7468)
        Pattern p = Pattern.compile(
                "-----BEGIN (?:.*)PRIVATE KEY-----\\s*(.*?)\\s*-----END (?:.*)PRIVATE KEY-----",
                Pattern.DOTALL
        );
        Matcher m = p.matcher(pem);
        if (!m.find()) {
            throw new IllegalStateException("PEM inválido: delimitadores de PRIVATE KEY não encontrados");
        }
        String base64Body = m.group(1);

        // Usa o decoder MIME (ignora \r, \n e espaços). Evita “incorrect ending byte”.
        byte[] keyBytes;
        try {
            keyBytes = Base64.getMimeDecoder().decode(base64Body);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Erro ao decodificar PEM Base64: " + ex.getMessage(), ex);
        }

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    /**
     * Exemplo de uso do cipher inicializado (opcional).
     */
    public String decryptBase64(String base64Ciphertext) throws Exception {
        byte[] ct = Base64.getDecoder().decode(base64Ciphertext);
        byte[] pt = cipher.doFinal(ct);
        return new String(pt, StandardCharsets.UTF_8);
    }
}
