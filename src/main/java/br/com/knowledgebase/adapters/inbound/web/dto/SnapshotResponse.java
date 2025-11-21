package br.com.knowledgebase.adapters.inbound.web.dto;

import br.com.knowledgebase.domain.model.Snapshot;

public record SnapshotResponse(
        Long id,
        String filename,
        String mimeType,
        String contentEncoding,
        Long gzipSizeBytes,
        long originalSizeBytes,
        String sha256Hex,
        String base64
) {
    public static SnapshotResponse from(Snapshot s){
        return new SnapshotResponse(
                s.id(),
                s.filename(),
                s.mimeType(),
                s.contentEncoding().value(),
                s.gzipSizeBytes(),
                s.originalSizeBytes(),
                s.sha256Hex(),
                s.base64()
        );
    }
}
