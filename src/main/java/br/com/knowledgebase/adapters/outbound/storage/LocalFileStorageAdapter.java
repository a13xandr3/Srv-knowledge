package br.com.knowledgebase.adapters.outbound.storage;

import br.com.knowledgebase.domain.ports.out.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Formatter;

@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path root;

    public LocalFileStorageAdapter(@Value("${app.storage.dir:storage}") String rootDir) throws IOException {
        this.root = Paths.get(rootDir).toAbsolutePath();
        Files.createDirectories(this.root);
    }

    @Override
    public String write(byte[] content, String filename) throws Exception {
        String hash = sha256Hex(content);
        Path folder = root.resolve(hash);                         // distribuição simples por hash
        String safeName = Paths.get(filename).getFileName().toString(); // sanitiza nome
        Path file = folder.resolve(hash + "_" + safeName);
        Files.createDirectories(folder);
        Files.write(file, content,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        return file.toString(); // caminho ficará em FileAsset.storagePath
    }

    @Override
    public byte[] read(String storagePath) throws Exception {
        return Files.readAllBytes(Path.of(storagePath));
    }

    private static String sha256Hex(byte[] bytes) throws Exception {
        var md = MessageDigest.getInstance("SHA-256");
        var digest = md.digest(bytes);
        try (var fmt = new Formatter()) {
            for (byte b : digest) fmt.format("%02x", b);
            return fmt.toString();
        }
    }
}
