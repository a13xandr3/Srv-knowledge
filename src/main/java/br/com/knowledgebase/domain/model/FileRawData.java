package br.com.knowledgebase.domain.model;

public record FileRawData(
        String filename,
        String mimeType,
        long sizeBytes,
        byte[] content
) {}