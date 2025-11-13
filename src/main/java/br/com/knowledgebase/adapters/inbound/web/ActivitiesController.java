package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.domain.ports.in.ActivityUseCase;
import br.com.knowledgebase.domain.model.Activity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atividade")
public class ActivitiesController {

    private final ActivityUseCase useCase;

    public ActivitiesController(ActivityUseCase useCase) {
        this.useCase = useCase;
    }

    @Tag(name = "Activities")
    @GetMapping
    @Operation(summary = "Lista paginada de atividades")
    public List<Activity> list(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(required = false) String q) {
        if (q != null && !q.isBlank()) {
            return useCase.search(q, page, size);
        }
        return useCase.list(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> get(@PathVariable Long id) {
        return useCase.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cria atividade")
    public Activity create(@RequestBody Activity a) {
        return useCase.create(a);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza atividade")
    public Activity update(@PathVariable Long id, @RequestBody Activity a) {
        return useCase.update(id, a);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove atividade")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
