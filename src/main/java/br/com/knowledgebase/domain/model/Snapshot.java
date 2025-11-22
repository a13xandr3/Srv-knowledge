package br.com.knowledgebase.domain.model;

public record Snapshot(
        Long id,
        String filename,
        String mimeType,
        ContentEncoding contentEncoding,
        Long gzipSizeBytes,
        long originalSizeBytes,
        String sha256Hex,
        String base64
) {}
