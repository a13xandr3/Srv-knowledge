package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.adapters.inbound.web.dto.FileSavedResponse;
import br.com.knowledgebase.domain.ports.in.FileUploadCommand;
import br.com.knowledgebase.domain.ports.in.FileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.ByteArrayInputStream;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/files")
public class FilesController {

    private final FileUseCase fileUseCase;

    public FilesController(FileUseCase fileUseCase) {
        this.fileUseCase = fileUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileSavedResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam @NotBlank String contentEncoding, // 'gzip' | 'identity'
            @RequestParam @NotBlank String hashSha256Hex,   // 64 chars
            @RequestParam long originalSizeBytes,
            @RequestParam(required = false) Long gzipSizeBytes,
            @RequestParam(required = false) String mimeType,
            @RequestParam(required = false) String filename
    ) throws IOException {

        // Converte HTTP → comando de domínio
        FileUploadCommand command = new FileUploadCommand(
                file.getBytes(),
                contentEncoding,
                hashSha256Hex,
                originalSizeBytes,
                gzipSizeBytes,
                mimeType,
                filename,
                file.getOriginalFilename(),
                file.getContentType()
        );

        try {
            FileSavedResponse result = fileUseCase.upload(command);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            // Regras de negócio inválidas → 400
            return ResponseEntity.badRequest().build();
        }
    }

    @Tag(name = "Files")
    @GetMapping("/{id}/raw")
    @Operation(summary = "Download de arquivo bruto por ID")
    public ResponseEntity<InputStreamResource> raw(@PathVariable Long id) throws Exception {

        var raw = fileUseCase.loadRaw(id); // domínio resolve metadados + storage

        var resource = new InputStreamResource(new ByteArrayInputStream(raw.content()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(raw.filename()))
                .contentType(MediaType.parseMediaType(raw.mimeType()))
                .contentLength(raw.sizeBytes())
                .body(resource);
    }

    private static String contentDisposition(String filename) {
        String ascii = filename.replace("\"", "");
        String enc = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        return "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + enc;
    }
}
