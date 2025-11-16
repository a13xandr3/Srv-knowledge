package br.com.knowledgebase.domain.ports.in;

import br.com.knowledgebase.adapters.inbound.web.dto.FileSavedResponse;
import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.model.FileRawData;

import java.util.List;
import java.util.Optional;

public interface FileUseCase {

    FileSavedResponse upload(FileUploadCommand command);

    Optional<FileAsset> get(Long id);

    FileRawData loadRaw(Long id) throws Exception;

}