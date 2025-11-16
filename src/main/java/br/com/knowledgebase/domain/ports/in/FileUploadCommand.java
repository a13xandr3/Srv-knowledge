package br.com.knowledgebase.domain.ports.in;

public record FileUploadCommand(
        byte[] payload,
        String contentEncoding,
        String hashSha256Hex,
        long originalSizeBytes,
        Long gzipSizeBytes,
        String requestedMimeType,
        String requestedFilename,
        String originalFilename,
        String originalContentType
) {}
