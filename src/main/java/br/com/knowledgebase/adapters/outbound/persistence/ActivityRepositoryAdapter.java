package br.com.knowledgebase.adapters.outbound.persistence;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.JpaActivityRepository;
import br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper.ActivityMapper;
import br.com.knowledgebase.domain.model.Activity;
import br.com.knowledgebase.domain.ports.out.ActivityRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ActivityRepositoryAdapter implements ActivityRepositoryPort {

    private final JpaActivityRepository jpa;

    public ActivityRepositoryAdapter(JpaActivityRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Activity save(Activity a) {
        var e = ActivityMapper.toJpa(a);
        var saved = jpa.save(e);
        return ActivityMapper.toDomain(saved);
    }

    @Override
    public Optional<Activity> findById(Long id) {
        return jpa.findById(id).map(ActivityMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Activity> list(int page, int size) {
        var pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jpa.findAll(pr)                     // Page<ActivityJpaEntity>
                .map(ActivityMapper::toDomain)   // Page<Activity>
                .getContent();                   // List<Activity>
    }

    @Override
    public List<Activity> search(String term, int page, int size) {
        var pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var q  = (term == null) ? "" : term.trim();
        // Se vazio, reutilize listagem paginada padrão
        if (q.isEmpty()) {
            return jpa.findAll(pr)
                    .map(ActivityMapper::toDomain)
                    .getContent();
        }
        return jpa.search(q, pr)              // Page<ActivityJpaEntity>
                .map(ActivityMapper::toDomain) // Page<Activity>
                .getContent();                // List<Activity>
    }

    @Override
    public List<String> findDistinctCategorias() {
        return jpa.findDistinctCategorias();
    }

}