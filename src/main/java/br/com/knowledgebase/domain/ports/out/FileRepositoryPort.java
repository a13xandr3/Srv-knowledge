package br.com.knowledgebase.domain.ports.out;

import br.com.knowledgebase.domain.model.FileAsset;

import java.util.List;
import java.util.Optional;

public interface FileRepositoryPort {
    FileAsset save(FileAsset f);
    Optional<FileAsset> findById(Long id);
    List<FileAsset> list(int page, int size);
}