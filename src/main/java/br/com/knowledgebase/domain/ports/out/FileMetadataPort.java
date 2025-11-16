package br.com.knowledgebase.domain.ports.out;

import br.com.knowledgebase.domain.model.FileData;

import java.util.Optional;

public interface FileMetadataPort {

    /**
     * Persiste metadados do upload e retorna o ID gerado.
     */

    Long saveUpload(
            String filename,
            String mimeType,
            String contentEncoding,
            String hashSha256Hex,
            long originalSizeBytes,
            Long gzipSizeBytes,
            byte[] payload
    );

    Optional<FileData> findById(Long id);
}
