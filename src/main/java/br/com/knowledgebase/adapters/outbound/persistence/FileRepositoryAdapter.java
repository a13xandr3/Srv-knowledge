package br.com.knowledgebase.adapters.outbound.persistence;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.JpaFileRepository;
import br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper.FileAssetMapper;
import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.ports.out.FileRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FileRepositoryAdapter implements FileRepositoryPort {

    private final JpaFileRepository jpa;

    public FileRepositoryAdapter(JpaFileRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public FileAsset save(FileAsset f) {
        var entity = FileAssetMapper.toJpa(f);
        var saved  = jpa.save(entity);
        return FileAssetMapper.toDomain(saved);
    }

    @Override
    public Optional<FileAsset> findById(Long id) {
        return jpa.findById(id).map(FileAssetMapper::toDomain);
    }

    @Override
    public List<FileAsset> list(int page, int size) {

        return jpa.findAll(PageRequest.of(page, size))
                .map(FileAssetMapper::toDomain)
                .getContent();
    }
}
