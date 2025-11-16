package br.com.knowledgebase.adapters.outbound.persistence;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.JpaFileRepository;
import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.FileAssetJpaEntity;
import br.com.knowledgebase.domain.model.FileData;
import br.com.knowledgebase.domain.ports.out.FileMetadataPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FileMetadataJpaAdapter implements FileMetadataPort {

    private final JpaFileRepository repository;

    public FileMetadataJpaAdapter(JpaFileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long saveUpload(
            String filename,
            String mimeType,
            String contentEncoding,
            String hashSha256Hex,
            long originalSizeBytes,
            Long gzipSizeBytes,
            byte[] payload
    ) {
        FileAssetJpaEntity e = new FileAssetJpaEntity();
        e.setFilename(filename);
        e.setMimeType(mimeType);
        e.setContentEncoding(contentEncoding);
        e.setSha256Hex(hashSha256Hex);
        e.setOriginalSizeBytes(originalSizeBytes);
        e.setGzipSizeBytes(gzipSizeBytes);
        e.setPayload(payload);
        return repository.save(e).getId();
    }

    @Override
    public Optional<FileData> findById(Long id) {
        return repository.findById(id)
                .map(e -> new FileData(
                        e.getId(),
                        e.getFilename(),
                        e.getMimeType(),
                        e.getContentEncoding(),
                        e.getSha256Hex(),
                        e.getOriginalSizeBytes(),
                        e.getGzipSizeBytes(),
                        e.getCreatedAt()
                ));
    }
}