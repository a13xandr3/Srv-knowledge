package br.com.knowledgebase.domain.ports.out;

public interface TotpProvisioningPort {
    String generateSecret();
    String otpauthUri(String issuer, String username, String secret);
}