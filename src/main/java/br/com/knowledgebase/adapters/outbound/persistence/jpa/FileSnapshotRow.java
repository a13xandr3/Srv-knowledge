package br.com.knowledgebase.adapters.outbound.persistence.jpa;

public record FileSnapshotRow(
        Long id,
        String filename,
        String mimeType,
        String contentEncoding,
        Long gzipSizeBytes,
        Long originalSizeBytes,
        String sha256Hex
) {}
