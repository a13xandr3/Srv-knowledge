package br.com.knowledgebase.application.usecases;

import org.springframework.stereotype.Service;

import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.ports.in.FileUseCase;
import br.com.knowledgebase.domain.ports.out.FileRepositoryPort;
import br.com.knowledgebase.domain.ports.out.FileStoragePort;

import java.security.MessageDigest;
import java.util.Formatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FileUseCaseImpl implements FileUseCase {

    private final FileRepositoryPort repo;
    private final FileStoragePort storage;

    public FileUseCaseImpl(FileRepositoryPort repo, FileStoragePort storage) {
        this.repo = repo;
        this.storage = storage;
    }

    @Override
    public FileAsset upload(String originalFilename, String mimeType, byte[] content, String hashMode) {
        try {
            String path = storage.write(content, originalFilename);
            FileAsset f = new FileAsset();
            f.setFilename(originalFilename);
            f.setMimeType(mimeType);
            f.setSizeBytes(content.length);
            f.setHashMode(hashMode);
            f.setHashSha256Hex(sha256Hex(content));
            f.setStoragePath(path);
            return repo.save(f);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao salvar arquivo", e);
        }
    }

    @Override
    public Optional<FileAsset> get(Long id) {
        return repo.findById(id);
    }

    @Override
    public byte[] downloadRaw(Long id) {
        var f = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Arquivo não encontrado"));
        try {
            return storage.read(f.getStoragePath());
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao ler arquivo", e);
        }
    }

    @Override
    public List<FileAsset> list(int page, int size) {
        return repo.list(page, size);
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            try (Formatter fmt = new Formatter()) {
                for (byte b : digest) fmt.format("%02x", b);
                return fmt.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
