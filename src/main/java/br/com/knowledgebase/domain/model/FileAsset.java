package br.com.knowledgebase.domain.model;

import java.time.LocalDateTime;

public class FileAsset {

    private Long id;
    private String filename;
    private String mimeType;
    private long sizeBytes;
    private Long gzipSizeBytes;
    private String hashSha256Hex;
    private String hashMode;
    private String storagePath;
    private LocalDateTime createdAt;

    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Long getGzipSizeBytes() {
        return gzipSizeBytes;
    }

    public void setGzipSizeBytes(Long gzipSizeBytes) {
        this.gzipSizeBytes = gzipSizeBytes;
    }

    public String getHashSha256Hex() {
        return hashSha256Hex;
    }

    public void setHashSha256Hex(String hashSha256Hex) {
        this.hashSha256Hex = hashSha256Hex;
    }

    public String getHashMode() {
        return hashMode;
    }

    public void setHashMode(String hashMode) {
        this.hashMode = hashMode;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}