package br.com.knowledgebase.adapters.outbound.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "files")
public class FileAssetJpaEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name="filename", nullable=false, length=255)
    private String filename;

    @Column(name="mimeType", length=255)
    private String mimeType;

    @Column(name="sizeBytes")
    private Long sizeBytes;

    @Column(name="gzipSizeBytes")
    private Long gzipSizeBytes;

    @Column(name="hashSha256Hex", length=128)
    private String hashSha256Hex;

    @Column(name="hashMode", length=32)
    private String hashMode;

    @Column(name="storagePath", length=512)
    private String storagePath;

    @Column(name="createdAt")
    private LocalDateTime createdAt;

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

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
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
