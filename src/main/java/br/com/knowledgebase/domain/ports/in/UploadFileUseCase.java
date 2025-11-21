package br.com.knowledgebase.domain.ports.in;

public interface UploadFileUseCase {
    record Command(
            String filename,
            String mimeType,
            String contentEncoding, // "gzip"|"identity"
            String hashSha256Hex,
            long originalSizeBytes,
            Long gzipSizeBytes,
            byte[] payload
    ) {}
    record Result(Long id, String hashSha256Hex) {}
    Result save(Command cmd);
}