package br.com.knowledgebase.domain.model;

import java.util.Objects;

public class FileAsset {
    private Long id;
    private final String filename;
    private final String mimeType;
    private final ContentEncoding contentEncoding;
    private final Long gzipSizeBytes;      // pode ser null quando identity
    private final long originalSizeBytes;
    private final String sha256Hex;        // 64 chars
    private final byte[] payload;          // binário persistido

    public FileAsset(
            Long id,
            String filename,
            String mimeType,
            ContentEncoding contentEncoding,
            Long gzipSizeBytes,
            long originalSizeBytes,
            String sha256Hex,
            byte[] payload
    ) {
        this.id = id;
        this.filename = Objects.requireNonNull(filename);
        this.mimeType = mimeType;
        this.contentEncoding = Objects.requireNonNull(contentEncoding);
        this.gzipSizeBytes = gzipSizeBytes;
        this.originalSizeBytes = originalSizeBytes;
        this.sha256Hex = Objects.requireNonNull(sha256Hex);
        this.payload = Objects.requireNonNull(payload);
    }

    public Long getId() { return id; }
    public void setId(Long id){ this.id = id; }
    public String getFilename(){ return filename; }
    public String getMimeType(){ return mimeType; }
    public ContentEncoding getContentEncoding(){ return contentEncoding; }
    public Long getGzipSizeBytes(){ return gzipSizeBytes; }
    public long getOriginalSizeBytes(){ return originalSizeBytes; }
    public String getSha256Hex(){ return sha256Hex; }
    public byte[] getPayload(){ return payload; }
}