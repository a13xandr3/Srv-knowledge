package br.com.knowledgebase.domain.ports.out;

import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.model.Snapshot;

import java.util.List;
import java.util.Optional;

public interface FileRepositoryPort {
    Long saveUpload(FileAsset file);
    Optional<FileAsset> findById(Long id);
    Optional<Snapshot> findSnapshotById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<FileAsset> findAllByIds(List<Long> ids);
    List<Snapshot> findSnapshotsByIds(List<Long> ids);
}
