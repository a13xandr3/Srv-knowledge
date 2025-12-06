package br.com.knowledgebase.adapters.outbound.persistence;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.JpaActivityRepository;
import br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper.ActivityMapper;
import br.com.knowledgebase.domain.model.Activity;
import br.com.knowledgebase.domain.model.TagWrapper;
import br.com.knowledgebase.domain.ports.out.ActivityRepositoryPort;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository; // <— opcionalmente use @Repository

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Repository // pode manter @Component se preferir, mas @Repository é mais adequado a adapters de persistência
public class ActivityRepositoryAdapter implements ActivityRepositoryPort {

    private final JpaActivityRepository jpa;
    private final ObjectMapper objectMapper;

    public ActivityRepositoryAdapter(JpaActivityRepository jpa, ObjectMapper objectMapper) {
        this.jpa = jpa;
        this.objectMapper = objectMapper;
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

    // >>> MÉTODO FALTANTE <<<
    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Activity> list(int page, int size) {
        var pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jpa.findAll(pr)
                .map(ActivityMapper::toDomain)
                .getContent();
    }

    @Override
    public List<Activity> search(String term, int page, int size) {
        var pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var q  = (term == null) ? "" : term.trim();
        if (q.isEmpty()) {
            return jpa.findAll(pr)
                    .map(ActivityMapper::toDomain)
                    .getContent();
        }
        return jpa.search(q, pr)
                .map(ActivityMapper::toDomain)
                .getContent();
    }

    @Override
    public List<String> findDistinctCategorias() {
        return jpa.findDistinctCategorias();
    }

    @Override
    public List<String> findDistinctTags() {
        return jpa.findAll().stream()
                .map(ActivityMapper::toDomain)
                .map(Activity::getTag)
                .filter(Objects::nonNull)
                .flatMap(tagJson -> {
                    try {
                        TagWrapper wrapper = objectMapper.convertValue(tagJson, TagWrapper.class);
                        return wrapper.tags() != null ? wrapper.tags().stream() : Stream.empty();
                    } catch (Exception e) {
                        return Stream.empty();
                    }
                })
                .distinct()
                .sorted()
                .toList();
    }
}
