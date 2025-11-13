package br.com.knowledgebase.domain.ports.out;

public interface TotpVerifierPort {
    boolean isValid(String secret, String code);
}