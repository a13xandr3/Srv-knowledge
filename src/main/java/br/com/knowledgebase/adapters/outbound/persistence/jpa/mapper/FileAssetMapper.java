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
        d.setSizeBytes(e.getSizeBytes() == null ? 0L : e.getSizeBytes());
        d.setGzipSizeBytes(e.getGzipSizeBytes());
        d.setHashSha256Hex(e.getHashSha256Hex());
        d.setHashMode(e.getHashMode());
        d.setStoragePath(e.getStoragePath());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }

    public static FileAssetJpaEntity toJpa(FileAsset d) {
        if (d == null) return null;
        var e = new FileAssetJpaEntity();
        e.setId(d.getId());
        e.setFilename(d.getFilename());
        e.setMimeType(d.getMimeType());
        e.setSizeBytes(d.getSizeBytes());
        e.setGzipSizeBytes(d.getGzipSizeBytes());
        e.setHashSha256Hex(d.getHashSha256Hex());
        e.setHashMode(d.getHashMode());
        e.setStoragePath(d.getStoragePath());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
