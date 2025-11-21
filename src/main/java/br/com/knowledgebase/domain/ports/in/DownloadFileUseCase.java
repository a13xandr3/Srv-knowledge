package br.com.knowledgebase.domain.ports.in;

public interface DownloadFileUseCase {
    record Downloaded(String filename, String mimeType, byte[] bytes) {}
    Downloaded downloadOriginal(Long id);
    Downloaded downloadGzip(Long id);
}