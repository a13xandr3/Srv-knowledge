package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.domain.ports.in.*;
import br.com.knowledgebase.domain.ports.out.FileRepositoryPort;
import br.com.knowledgebase.domain.exception.FileNotFoundException;
import br.com.knowledgebase.domain.model.ContentEncoding;
import br.com.knowledgebase.domain.model.FileAsset;
import br.com.knowledgebase.domain.model.Snapshot;
import static br.com.knowledgebase.domain.service.CompressionService.*;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileService implements
        UploadFileUseCase,
        GetSnapshotUseCase,
        BatchSnapshotsUseCase,
        DownloadFileUseCase,
        DeleteFileUseCase {

    private final FileRepositoryPort repo;

    public FileService(FileRepositoryPort repo){ this.repo = repo; }

    @Override
    public UploadFileUseCase.Result save(UploadFileUseCase.Command c) {
        // validações mínimas (S de SRP + fail-fast)
        if (c.hashSha256Hex() == null || c.hashSha256Hex().length() != 64)
            throw new IllegalArgumentException("hashSha256Hex must be 64 hex chars");

        ContentEncoding enc = ContentEncoding.from(c.contentEncoding());
        String filename = (c.filename() != null && !c.filename().isBlank())
                ? c.filename()
                : "file.bin";
        String mimeType = (c.mimeType() != null && !c.mimeType().isBlank())
                ? c.mimeType()
                : "application/octet-stream";

        var asset = new FileAsset(
                null,
                filename,
                mimeType,
                enc,
                c.gzipSizeBytes(),
                c.originalSizeBytes(),
                c.hashSha256Hex(),
                Objects.requireNonNull(c.payload(), "payload is null")
        );
        Long id = repo.saveUpload(asset);
        return new UploadFileUseCase.Result(id, c.hashSha256Hex());
    }

    @Override
    public Snapshot get(Long id, boolean includeBase64) {
        FileAsset fe = repo.findById(id).orElseThrow(() -> new FileNotFoundException(id));
        String base64 = includeBase64 ? Base64.getEncoder().encodeToString(fe.getPayload()) : null;
        return new Snapshot(
                fe.getId(), fe.getFilename(), fe.getMimeType(),
                fe.getContentEncoding(), fe.getGzipSizeBytes(),
                fe.getOriginalSizeBytes(), fe.getSha256Hex(), base64
        );
    }

    @Override
    public List<Snapshot> getAllPreservingOrder(List<Long> ids, boolean includeBase64) {
        if (ids == null || ids.isEmpty()) return List.of();
        var list = repo.findAllByIds(ids);
        var map = new HashMap<Long, FileAsset>(list.size());
        for (var fe : list) map.put(fe.getId(), fe);

        var out = new ArrayList<Snapshot>(ids.size());
        for (Long id : ids) {
            var fe = map.get(id);
            if (fe == null) continue;
            String base64 = includeBase64 ? Base64.getEncoder().encodeToString(fe.getPayload()) : null;
            out.add(new Snapshot(
                    fe.getId(), fe.getFilename(), fe.getMimeType(),
                    fe.getContentEncoding(), fe.getGzipSizeBytes(),
                    fe.getOriginalSizeBytes(), fe.getSha256Hex(), base64
            ));
        }
        return out;
    }

    @Override
    public Downloaded downloadOriginal(Long id) {
        FileAsset fe = repo.findById(id).orElseThrow(() -> new FileNotFoundException(id));
        byte[] bytes = (fe.getContentEncoding() == ContentEncoding.GZIP) ? gunzip(fe.getPayload()) : fe.getPayload();
        return new Downloaded(fe.getFilename(), safeMime(fe.getMimeType()), bytes);
    }

    @Override
    public Downloaded downloadGzip(Long id) {
        FileAsset fe = repo.findById(id).orElseThrow(() -> new FileNotFoundException(id));
        byte[] gz = (fe.getContentEncoding() == ContentEncoding.GZIP) ? fe.getPayload() : gzip(fe.getPayload());
        return new Downloaded(fe.getFilename() + ".gz", "application/gzip".intern(), gz);
    }

    @Override
    public void deleteById(Long id) {
        // idempotente/404 mantido para o adapter mapear
        repo.deleteById(id);
    }

    private static String safeMime(String mt){
        if (mt == null || mt.isBlank()) return "application/octet-stream";
        try { return org.springframework.http.MediaType.parseMediaType(mt).toString(); }
        catch (Exception e){ return "application/octet-stream"; }
    }
}
