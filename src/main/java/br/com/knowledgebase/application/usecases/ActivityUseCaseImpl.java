package br.com.knowledgebase.application.usecases;

import br.com.knowledgebase.domain.ports.in.ActivityFilterParams;
import br.com.knowledgebase.domain.ports.in.ActivityPageResult;
import org.springframework.stereotype.Service;

import br.com.knowledgebase.domain.model.Activity;
import br.com.knowledgebase.domain.ports.in.ActivityUseCase;
import br.com.knowledgebase.domain.ports.out.ActivityRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ActivityUseCaseImpl implements ActivityUseCase {

    private final ActivityRepositoryPort repo;

    public ActivityUseCaseImpl(ActivityRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Activity create(Activity a) {
        return repo.save(a);
    }

    @Override
    public Optional<Activity> get(Long id) {
        return repo.findById(id);
    }

    @Override
    public Activity update(Long id, Activity a) {
        final var now = LocalDateTime.now();
        return repo.findById(id).map(exist -> {
            exist.updateFrom(a, now);
            return repo.save(exist);
        }).orElseThrow(() -> new NoSuchElementException("Atividade não encontrada"));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Activity> list(int page, int size) {
        return repo.list(page, size);
    }

    @Override
    public List<Activity> search(String term, int page, int size) {
        return repo.search(term, page, size);
    }

    @Override
    public ActivityPageResult listWithFilters(ActivityFilterParams filterParams, int page, int size) {

        // ponto único para colocar sua REGRA de filtro/paginação
        // por enquanto, exemplo simples delegando para list(...)
        // depois você pode encaixar aqui a mesma lógica que estava no controller.

        List<Activity> activities = list(page, size);

        int total = activities.size(); // ou use o count do repositório, se tiver

        return new ActivityPageResult(
                activities,
                total,
                page,
                size
        );
    }
}
