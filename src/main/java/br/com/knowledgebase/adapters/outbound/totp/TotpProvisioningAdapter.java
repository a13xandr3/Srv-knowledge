package br.com.knowledgebase.adapters.outbound.totp;

import br.com.knowledgebase.adapters.outbound.security.TotpUtil;
import br.com.knowledgebase.domain.ports.out.TotpProvisioningPort;
import org.springframework.stereotype.Component;

@Component
public class TotpProvisioningAdapter implements TotpProvisioningPort {
    @Override
    public String generateSecret() {
        return TotpUtil.randomBase32();
    }
    @Override
    public String otpauthUri(String issuer, String username, String secret) {
        return TotpUtil.otpauthUri(issuer, username, secret);
    }
}