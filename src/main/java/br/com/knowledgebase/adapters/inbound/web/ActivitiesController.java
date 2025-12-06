package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.adapters.inbound.web.dto.ActivityPageResponse;
import br.com.knowledgebase.domain.model.Activity;
import br.com.knowledgebase.domain.ports.in.ActivityFilterParams;
import br.com.knowledgebase.domain.ports.in.ActivityPageResult;
import br.com.knowledgebase.domain.ports.in.ActivityUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@Tag(name = "Activities", description = "Operações de consulta e manutenção de atividades")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/atividades")
public class ActivitiesController {

    private final ActivityUseCase activityUseCase;

    public ActivitiesController(ActivityUseCase activityUseCase) {
        this.activityUseCase = activityUseCase;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lista atividades com filtros e paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ActivityPageResponse.class)))
    })
    public ResponseEntity<ActivityPageResponse> getAllAtividades(
            @Parameter(description = "Página (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamanho da página (1..100)") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit,
            @Parameter(description = "Categorias a excluir") @RequestParam(name = "excessao", required = false) List<String> excessao,
            @Parameter(description = "Categorias para incluir") @RequestParam(name = "categoria", required = false) List<String> categoria,
            @Parameter(description = "Tags para incluir") @RequestParam(name = "tag", required = false) List<String> tag,
            @Parameter(description = "Termo de busca em categoria") @RequestParam(name = "categoriaTerm", required = false) String categoriaTerm
    ) {
        ActivityFilterParams filterParams = new ActivityFilterParams(
                excessao, categoria, tag, categoriaTerm
        );
        ActivityPageResult result = activityUseCase.listWithFilters(filterParams, page, limit);
        ActivityPageResponse body = new ActivityPageResponse(
                result.activities(), result.total(), result.page(), result.size()
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Busca atividade por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Activity.class))),
            @ApiResponse(responseCode = "404", description = "Não encontrada")
    })
    public ResponseEntity<Activity> get(@PathVariable Long id) {
        return activityUseCase.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cria atividade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criada",
                    content = @Content(schema = @Schema(implementation = Activity.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    public ResponseEntity<Activity> create(@RequestBody Activity a,
                                           UriComponentsBuilder uriBuilder) {
        Activity saved = activityUseCase.create(a);
        // assume que Activity possui getId(); ajuste se o nome do getter for diferente
        URI location = uriBuilder.path("/api/atividades/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualiza atividade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizada",
                    content = @Content(schema = @Schema(implementation = Activity.class))),
            @ApiResponse(responseCode = "404", description = "Não encontrada")
    })
    public ResponseEntity<Activity> update(@PathVariable Long id, @RequestBody Activity a) {
        Activity updated = activityUseCase.update(id, a);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/delete")
    @Operation(summary = "Exclui Link e Files de forma atômica")
    public ResponseEntity<Void> deleteComposite(@RequestBody DeleteCompositeRequest body) {
        activityUseCase.deleteLinkAndFiles(body.linkId(), body.fileIds());
        return ResponseEntity.noContent().build(); // 204
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove atividade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removida"),
            @ApiResponse(responseCode = "404", description = "Não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/categorias", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lista categorias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    public ResponseEntity<List<String>> getCategorias(
            @Parameter(description = "Categorias a excluir")
            @RequestParam(name = "excessao", required = false) List<String> excessao
    ) {
        final var categorias = activityUseCase.listarCategorias(excessao);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lista tags de atividades")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    public ResponseEntity<List<String>> getTags(
            @Parameter(description = "Categorias a excluir")
            @RequestParam(name = "excessao", required = false) List<String> excessao
    ) {
        List<String> tags = activityUseCase.listarTags(excessao);
        return ResponseEntity.ok(tags);
    }

    public record DeleteCompositeRequest(Long linkId, java.util.List<Long> fileIds) {}

}
