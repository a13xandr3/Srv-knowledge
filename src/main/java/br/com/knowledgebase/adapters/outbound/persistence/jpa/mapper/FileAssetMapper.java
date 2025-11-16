package br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.FileAssetJpaEntity;
import br.com.knowledgebase.domain.model.FileAsset;

public final class FileAssetMapper {

    private FileAssetMapper() {}

    public static FileAsset toDomain(FileAssetJpaEntity e) {
        if (e == null) return null;
        var d = new FileAsset();
        d.setId(e.getId());
        d.setFilename(e.getFilename());
        d.setMimeType(e.getMimeType());
        d.setContentEncoding(e.getContentEncoding());
        d.setSha256Hex(e.getSha256Hex());
        d.setOriginalSizeBytes(e.getOriginalSizeBytes() == null ? 0L : e.getOriginalSizeBytes());
        d.setGzipSizeBytes(e.getGzipSizeBytes());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }

    public static FileAssetJpaEntity toJpa(FileAsset d) {
        if (d == null) return null;
        var e = new FileAssetJpaEntity();
        e.setId(d.getId());
        e.setFilename(d.getFilename());
        e.setMimeType(d.getMimeType());
        e.setContentEncoding(d.getContentEncoding());
        e.setSha256Hex(d.getSha256Hex());
        e.setOriginalSizeBytes(d.getOriginalSizeBytes());
        e.setGzipSizeBytes(d.getGzipSizeBytes());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}