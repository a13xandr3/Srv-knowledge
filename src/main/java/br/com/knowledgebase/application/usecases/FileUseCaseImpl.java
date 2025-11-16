package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.adapters.inbound.web.dto.FileSavedResponse;
import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.model.FileRawData;
import br.com.knowledgebase.domain.ports.in.FileUploadCommand;
import br.com.knowledgebase.domain.ports.in.FileUseCase;
import br.com.knowledgebase.domain.ports.out.FileMetadataPort;
import br.com.knowledgebase.domain.ports.out.FileRepositoryPort;
import br.com.knowledgebase.domain.ports.out.FileStoragePort;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileUseCaseImpl implements FileUseCase {

    private final FileRepositoryPort repo;
    private final FileStoragePort storage;
    private final FileMetadataPort metadataPort;

    public FileUseCaseImpl(FileRepositoryPort repo, FileStoragePort storage, FileMetadataPort metadataPort) {
        this.repo = repo;
        this.storage = storage;
        this.metadataPort = metadataPort;
    }

    @Override
    public FileSavedResponse upload(FileUploadCommand cmd) {

        // 1) Validação de negócio
        final String enc = safeLower(cmd.contentEncoding());
        if (!enc.equals("gzip") && !enc.equals("identity")) {
            throw new IllegalArgumentException("contentEncoding inválido: " + cmd.contentEncoding());
        }

        if (cmd.hashSha256Hex() == null || cmd.hashSha256Hex().length() != 64) {
            throw new IllegalArgumentException("hashSha256Hex inválido. Esperado 64 caracteres.");
        }

        // 2) Defaults de filename
        final String fname = resolveFilename(
                cmd.requestedFilename(),
                cmd.originalFilename()
        );

        // 3) Defaults de mimeType
        final String mt = resolveMimeType(
                cmd.requestedMimeType(),
                cmd.originalContentType()
        );

        // 4) Grava o arquivo físico via FileStoragePort
        final String storagePath;
        try {
            storagePath = storage.write(cmd.payload(), fname);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gravar conteúdo do arquivo no storage", e);
        }

        // 5) Persiste metadados + caminho no DB via FileMetadataPort
        Long id = metadataPort.saveUpload(
                fname,
                mt,
                enc,
                cmd.hashSha256Hex(),
                cmd.originalSizeBytes(),
                cmd.gzipSizeBytes(),
                cmd.payload()
        );

        // 6) Retorno do caso de uso
        return new FileSavedResponse(id, cmd.hashSha256Hex());
    }

    private String safeLower(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String resolveFilename(String requested, String original) {
        if (requested != null && !requested.isBlank()) {
            return requested;
        }
        if (original != null && !original.isBlank()) {
            return original;
        }
        return "file.bin";
    }

    private String resolveMimeType(String requested, String originalContentType) {
        if (requested != null && !requested.isBlank()) {
            return requested;
        }
        if (originalContentType != null && !originalContentType.isBlank()) {
            return originalContentType;
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    @Override
    public Optional<FileAsset> get(Long id) {
        return repo.findById(id);
    }

    @Override
    public FileRawData loadRaw(Long id) throws Exception {
        var meta = metadataPort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Arquivo não encontrado: " + id));

        // Aqui a infra descobre "onde" está o arquivo usando a chave de domínio (sha256)
        byte[] bytes = storage.read(meta.sha256Hex());

        String filename = meta.filename() != null ? meta.filename() : ("file-" + id);
        String mimeType = meta.mimeType() != null ? meta.mimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        long size = (meta.originalSizeBytes() != null && meta.originalSizeBytes() > 0)
                ? meta.originalSizeBytes()
                : bytes.length;

        return new FileRawData(filename, mimeType, size, bytes);
    }
}