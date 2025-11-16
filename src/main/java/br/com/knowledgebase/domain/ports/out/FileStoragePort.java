package br.com.knowledgebase.domain.ports.out;

public interface FileStoragePort {
    /**
     * Escreve o conteúdo e retorna o caminho físico persistido (absoluto ou relativo, conforme config).
     */
    String write(byte[] content, String filename) throws Exception;

    /**
     * Lê todos os bytes do caminho físico previamente salvo.
     */
    byte[] read(String storagePath) throws Exception;
}