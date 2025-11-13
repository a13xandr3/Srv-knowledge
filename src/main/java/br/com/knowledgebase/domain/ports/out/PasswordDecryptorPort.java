package br.com.knowledgebase.domain.ports.out;

public interface PasswordDecryptorPort {
    String decryptBase64(String ciphertextB64);
}