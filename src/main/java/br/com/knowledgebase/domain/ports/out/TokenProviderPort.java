package br.com.knowledgebase.domain.ports.out;

public interface TokenProviderPort {
    String generate(String subject);
    boolean validate(String token);
    String subject(String token); // ou getSubject(...)
}