package br.com.knowledgebase.domain.ports.out;

import br.com.knowledgebase.domain.model.FileAsset;

import java.util.List;
import java.util.Optional;

public interface FileRepositoryPort {
    Long saveUpload(FileAsset file);
    Optional<FileAsset> findById(Long id);
    void deleteById(Long id);
    List<FileAsset> findAllByIds(List<Long> ids);
}