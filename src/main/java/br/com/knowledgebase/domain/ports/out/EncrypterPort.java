package br.com.knowledgebase.domain.ports.out;

public interface EncrypterPort {
    String hash(String raw);
    boolean matches(String raw, String hash);
}