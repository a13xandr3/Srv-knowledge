package br.com.knowledgebase.security;

public interface PasswordDecryptor {
    String decrypt(String base64Ciphertext);
}
