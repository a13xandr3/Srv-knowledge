package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.domain.ports.in.FileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/files")
public class FilesController {

    private final FileUseCase useCase;

    public FilesController(FileUseCase useCase) {
        this.useCase = useCase;
    }

    @Tag(name = "Files")
    @GetMapping("/{id}/raw")
    @Operation(summary = "Download de arquivo bruto por ID")
    public ResponseEntity<InputStreamResource> raw(@PathVariable Long id) throws IOException {
        var f = useCase.get(id).orElseThrow(() -> new java.util.NoSuchElementException("Arquivo não encontrado: " + id));
        Path p = Paths.get(f.getStoragePath());
        if (!Files.exists(p)) throw new java.util.NoSuchElementException("Conteúdo físico não encontrado");

        var res = new InputStreamResource(Files.newInputStream(p));
        var filename = f.getFilename() != null ? f.getFilename() : ("file-" + id);
        var contentType = f.getMimeType() != null ? f.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(filename))
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(Files.size(p))
                .body(res);
    }

    private static String contentDisposition(String filename) {
        String ascii = filename.replace("\"", "");
        String enc = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        return "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + enc;
    }
}
