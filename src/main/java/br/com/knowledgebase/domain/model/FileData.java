package br.com.knowledgebase.domain.model;

import java.time.LocalDateTime;

public record FileData(
        Long id,
        String filename,
        String mimeType,
        String contentEncoding,
        String sha256Hex,
        Long originalSizeBytes,
        Long gzipSizeBytes,
        LocalDateTime createdAt
    ) {}
