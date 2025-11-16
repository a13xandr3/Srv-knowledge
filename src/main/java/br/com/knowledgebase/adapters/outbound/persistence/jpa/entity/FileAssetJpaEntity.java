package br.com.knowledgebase.adapters.outbound.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "files")
public class FileAssetJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="filename", nullable=false, length=255)
    private String filename;

    @Column(name="mime_type", length=255)
    private String mimeType;

    @Column(name="content_encoding", nullable = false, length = 16)
    private String contentEncoding;

    @Column(name="sha256_hex", nullable = false, unique = true, length=64)
    private String sha256Hex;

    @Column(name="original_size_bytes", nullable = false)
    private Long originalSizeBytes;

    @Column(name="gzip_size_bytes")
    private Long gzipSizeBytes;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "payload", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] payload;

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

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getSha256Hex() {
        return sha256Hex;
    }

    public void setSha256Hex(String sha256Hex) {
        this.sha256Hex = sha256Hex;
    }

    public Long getOriginalSizeBytes() {
        return originalSizeBytes;
    }

    public void setOriginalSizeBytes(Long originalSizeBytes) {
        this.originalSizeBytes = originalSizeBytes;
    }

    public Long getGzipSizeBytes() {
        return gzipSizeBytes;
    }

    public void setGzipSizeBytes(Long gzipSizeBytes) {
        this.gzipSizeBytes = gzipSizeBytes;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
