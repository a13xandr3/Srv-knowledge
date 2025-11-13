package br.com.knowledgebase.domain.ports.in;

import br.com.knowledgebase.domain.model.FileAsset;

import java.util.List;
import java.util.Optional;

public interface FileUseCase {
    FileAsset upload(String originalFilename, String mimeType, byte[] content, String hashMode);
    Optional<FileAsset> get(Long id);
    byte[] downloadRaw(Long id);
    List<FileAsset> list(int page, int size);
}
