package br.com.knowledgebase.domain.ports.out;

public interface FileStoragePort {
    String write(byte[] content, String filename) throws Exception;
    byte[] read(String storagePath) throws Exception;
    void delete(String storagePath) throws Exception;
}