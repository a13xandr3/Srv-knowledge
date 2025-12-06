package br.com.knowledgebase.adapters.outbound.persistence;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.SpringDataFileJpaRepository;
import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.FileAssetJpaEntity;
import br.com.knowledgebase.domain.model.ContentEncoding;
import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.ports.out.FileRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FileRepositoryJpaAdapter implements FileRepositoryPort {

    private final SpringDataFileJpaRepository repo;

    public FileRepositoryJpaAdapter(SpringDataFileJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Long saveUpload(FileAsset f) {
        FileAssetJpaEntity e = toEntity(f);
        // Se id for gerado pelo banco, garanta que e.setId(null) para inserts
        // e.setId(null);
        return repo.save(e).getId();
    }

    @Override
    public Optional<FileAsset> findById(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    @Override
    public List<FileAsset> findAllByIds(List<Long> ids) {
        return repo.findAllById(ids).stream()
                .map(this::toDomain)
                .toList();
    }

    // ========== MAPEAMENTOS PRIVADOS ==========

    private FileAsset toDomain(FileAssetJpaEntity e) {
        // Ordem e tipos alinhados ao construtor que você já usou no primeiro findById:
        // (id, filename, mimeType, ContentEncoding, gzipSizeBytes, originalSizeBytes, sha256Hex, payload)
        return new FileAsset(
                e.getId(),
                e.getFilename(),
                e.getMimeType(),
                ContentEncoding.from(e.getContentEncoding()),
                e.getGzipSizeBytes(),
                e.getOriginalSizeBytes(),
                e.getSha256Hex(),
                e.getPayload()
        );
    }

    private FileAssetJpaEntity toEntity(FileAsset d) {
        FileAssetJpaEntity e = new FileAssetJpaEntity();

        // Se seu fluxo suportar update com id explícito, mantenha. Para insert com @GeneratedValue, remova.
        if (d.getId() != null) {
            e.setId(d.getId());
        }

        e.setFilename(d.getFilename());
        e.setMimeType(d.getMimeType());
        e.setContentEncoding(
                d.getContentEncoding() != null ? d.getContentEncoding().value() : null
        );
        e.setSha256Hex(d.getSha256Hex());
        e.setOriginalSizeBytes(d.getOriginalSizeBytes());
        e.setGzipSizeBytes(d.getGzipSizeBytes());
        e.setPayload(d.getPayload());
        return e;
    }
}

