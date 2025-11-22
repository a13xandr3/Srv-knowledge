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

    public FileRepositoryJpaAdapter(SpringDataFileJpaRepository repo){ this.repo = repo; }

    @Override
    public Long saveUpload(FileAsset f) {
        var e = new FileAssetJpaEntity();
        e.setFilename(f.getFilename());
        e.setMimeType(f.getMimeType());
        e.setContentEncoding(f.getContentEncoding().value());
        e.setGzipSizeBytes(f.getGzipSizeBytes());
        e.setOriginalSizeBytes(f.getOriginalSizeBytes());
        e.setSha256Hex(f.getSha256Hex());
        e.setPayload(f.getPayload());
        return repo.save(e).getId();
    }

    @Override
    public Optional<FileAsset> findById(Long id) {
        return repo.findById(id).map(e -> new FileAsset(
                e.getId(), e.getFilename(), e.getMimeType(),
                ContentEncoding.from(e.getContentEncoding()),
                e.getGzipSizeBytes(), e.getOriginalSizeBytes(),
                e.getSha256Hex(), e.getPayload()
        ));
    }

    @Override
    public void deleteById(Long id) { repo.deleteById(id); }

    @Override
    public List<FileAsset> findAllByIds(List<Long> ids) {
        return repo.findAllById(ids).stream().map(e -> new FileAsset(
                e.getId(), e.getFilename(), e.getMimeType(),
                ContentEncoding.from(e.getContentEncoding()),
                e.getGzipSizeBytes(), e.getOriginalSizeBytes(),
                e.getSha256Hex(), e.getPayload()
        )).toList();
    }
}
