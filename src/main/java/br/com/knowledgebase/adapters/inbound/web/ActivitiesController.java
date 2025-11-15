package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.domain.ports.in.ActivityFilterParams;
import br.com.knowledgebase.domain.ports.in.ActivityPageResult;
import br.com.knowledgebase.domain.ports.in.ActivityUseCase;
import br.com.knowledgebase.domain.model.Activity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/atividade")
@SecurityRequirement(name = "BearerAuth")
public class ActivitiesController {

    private final ActivityUseCase activityUseCase;

    public ActivitiesController(ActivityUseCase useCase, ActivityUseCase activityUseCase) {
        this.activityUseCase = activityUseCase;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAtividades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(name = "excessao",      required = false) List<String> excessao,
            @RequestParam(name = "categoria",     required = false) List<String> categoria,
            @RequestParam(name = "tag",           required = false) List<String> tag,
            @RequestParam(name = "categoriaTerm", required = false) String categoriaTerm
    ) {
        // Apenas monta o objeto de filtro e delega para o use case
        ActivityFilterParams filterParams = new ActivityFilterParams(
                excessao,
                categoria,
                tag,
                categoriaTerm
        );

        ActivityPageResult result = activityUseCase.listWithFilters(filterParams, page, limit);

        var body = Map.<String, Object>of(
                "atividades", result.activities(),  // <-- sem map(this::toRecord)
                "total", result.total(),
                "page", result.page(),
                "limit", result.size()
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> get(@PathVariable Long id) {
        return activityUseCase.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cria atividade")
    public Activity create(@RequestBody Activity a) {
        return activityUseCase.create(a);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza atividade")
    public Activity update(@PathVariable Long id, @RequestBody Activity a) {
        return activityUseCase.update(id, a);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove atividade")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
