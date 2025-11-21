package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.adapters.inbound.web.dto.FileSavedResponse;
import br.com.knowledgebase.adapters.inbound.web.dto.IdsRequest;
import br.com.knowledgebase.adapters.inbound.web.dto.SnapshotResponse;
import br.com.knowledgebase.domain.model.Snapshot;
import br.com.knowledgebase.domain.ports.in.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@Validated
@Tag(name = "Files", description = "Operações de upload, download e snapshots de arquivos")
@SecurityRequirement(name = "bearerAuth") // se você definiu bearerAuth no OpenApiConfig
public class FileController {

    private final UploadFileUseCase uploadUC;
    private final DeleteFileUseCase deleteUC;
    private final GetSnapshotUseCase snapshotUC;
    private final BatchSnapshotsUseCase batchSnapshotsUC;
    private final DownloadFileUseCase downloadUC;

    public FileController(UploadFileUseCase uploadUC,
                          DeleteFileUseCase deleteUC,
                          GetSnapshotUseCase snapshotUC,
                          BatchSnapshotsUseCase batchSnapshotsUC,
                          DownloadFileUseCase downloadUC) {
        this.uploadUC = uploadUC;
        this.deleteUC = deleteUC;
        this.snapshotUC = snapshotUC;
        this.batchSnapshotsUC = batchSnapshotsUC;
        this.downloadUC = downloadUC;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Upload de arquivo binário",
            description = "Recebe um arquivo e metadados; persiste o payload exatamente como veio do front."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo salvo",
                    content = @Content(schema = @Schema(implementation = FileSavedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<FileSavedResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam @NotBlank @Pattern(regexp = "gzip|identity") String contentEncoding,
            @RequestParam @NotBlank @Pattern(regexp = "^[0-9a-fA-F]{64}$") String hashSha256Hex,
            @RequestParam long originalSizeBytes,
            @RequestParam(required = false) Long gzipSizeBytes,
            @RequestParam(required = false) String mimeType,
            @RequestParam(required = false) String filename
    ) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            // Deixe o @ControllerAdvice mapear para 400/500 conforme sua política
            throw new IllegalStateException("Falha ao ler bytes do arquivo multipart", e);
        }

        var cmd = new UploadFileUseCase.Command(
                filename,
                mimeType,
                contentEncoding,
                hashSha256Hex,
                originalSizeBytes,
                gzipSizeBytes,
                bytes
        );
        var res = uploadUC.save(cmd);
        return ResponseEntity.ok(new FileSavedResponse(res.id(), res.hashSha256Hex()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir arquivo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído"),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obter snapshot (metadados e Base64 opcional)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = SnapshotResponse.class))),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    })
    public ResponseEntity<SnapshotResponse> snapshot(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean includeBase64
    ) {
        Snapshot s = snapshotUC.get(id, includeBase64);
        return ResponseEntity.ok(SnapshotResponse.from(s));
    }

    @GetMapping(value = "/{id}/download", produces = MediaType.ALL_VALUE)
    @Operation(summary = "Download do arquivo original (descompacta se 'gzip')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "*/*",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    })
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        var d = downloadUC.downloadOriginal(id);
        MediaType mt;
        try {
            mt = MediaType.parseMediaType(d.mimeType());
        } catch (Exception e) {
            mt = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(mt)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(d.filename()))
                .body(d.bytes());
    }

    @GetMapping(value = "/{id}/download-gzip", produces = "application/gzip")
    @Operation(summary = "Download do payload em GZIP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/gzip",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    })
    public ResponseEntity<byte[]> downloadGzip(@PathVariable Long id) {
        var d = downloadUC.downloadGzip(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/gzip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(d.filename()))
                .body(d.bytes());
    }

    @GetMapping(value = "/batch/snapshots", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Batch de snapshots (GET por querystring)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SnapshotResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Lista de IDs vazia/ausente")
    })
    public ResponseEntity<List<SnapshotResponse>> batchSnapshotsGet(
            @RequestParam @NotEmpty List<Long> ids,
            @RequestParam(defaultValue = "false") boolean includeBase64
    ) {
        return ResponseEntity.ok(
                batchSnapshotsUC.getAllPreservingOrder(ids, includeBase64)
                        .stream().map(SnapshotResponse::from).toList()
        );
    }

    @PostMapping(path = "/batch/snapshots",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Batch de snapshots (POST por JSON)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SnapshotResponse.class)))),
            @ApiResponse(responseCode = "400", description = "JSON inválido ou lista vazia")
    })
    public ResponseEntity<List<SnapshotResponse>> batchSnapshotsPost(@Valid @RequestBody IdsRequest req) {
        boolean includeBase64 = Boolean.TRUE.equals(req.includeBase64());
        return ResponseEntity.ok(
                batchSnapshotsUC.getAllPreservingOrder(req.ids(), includeBase64)
                        .stream().map(SnapshotResponse::from).toList()
        );
    }

    private static String contentDisposition(String filename) {
        // RFC 5987/6266 — nomes UTF-8 seguros
        return ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build().toString();
    }
}
