package br.com.knowledgebase.adapters.outbound.persistence;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.JpaActivityRepository;
import br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper.ActivityMapper;
import br.com.knowledgebase.domain.model.Activity;
import br.com.knowledgebase.domain.model.TagWrapper;
import br.com.knowledgebase.domain.ports.in.ActivityFilterParams;
import br.com.knowledgebase.domain.ports.out.ActivityRepositoryPort;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<Activity> findWithFilters(ActivityFilterParams filterParams, Pageable pageable) {

        // --- 1) Normaliza filtros de entrada ---
        var excessao      = filterParams.excessao()      != null ? filterParams.excessao()      : List.<String>of();
        var categorias    = filterParams.categoria()     != null ? filterParams.categoria()     : List.<String>of();
        var tagsFiltroRaw = filterParams.tag()           != null ? filterParams.tag()           : List.<String>of();
        var categoriaTerm = filterParams.categoriaTerm();

        // Mesma ideia do toNormalizedSet do ActivityUseCaseImpl
        var excNorm          = toNormalizedSet(excessao);
        var categoriasFiltro = toNormalizedSet(categorias);
        var tagsFiltroSet    = toNormalizedSet(tagsFiltroRaw);
        var term             = ActivityFilterParams.normalize(categoriaTerm);

        // --- 2) Carrega TODAS as atividades já ordenadas por createdAt desc ---
        var allActivities = jpa.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(ActivityMapper::toDomain)
                .toList();

        // --- 3) Aplica os mesmos filtros que existiam no controller antigo ---

        var filtrado = allActivities.stream()
                // excluir por categoria (lista de exceções)
                .filter(a -> excNorm.isEmpty()
                        || !excNorm.contains(ActivityFilterParams.normalize(a.getCategoria())))
                // incluir por categoria (se informado)
                .filter(a -> categoriasFiltro.isEmpty()
                        || categoriasFiltro.contains(ActivityFilterParams.normalize(a.getCategoria())))
                // incluir por tags (se informado) — requer interseção não-vazia
                .filter(a -> {
                    if (tagsFiltroSet.isEmpty()) return true;

                    var tagJson = a.getTag(); // Map<String, Object> conforme Activity
                    if (tagJson == null) return false;

                    try {
                        TagWrapper wrapper = objectMapper.convertValue(tagJson, TagWrapper.class);
                        var atvTags = wrapper.tags();
                        if (atvTags == null || atvTags.isEmpty()) return false;

                        var atvTagsNorm = atvTags.stream()
                                .filter(Objects::nonNull)
                                .map(String::strip)
                                .filter(s -> !s.isBlank())
                                .map(ActivityFilterParams::normalize)
                                .collect(java.util.stream.Collectors.toSet());

                        // precisa haver interseção entre tags do filtro e tags da atividade
                        return !java.util.Collections.disjoint(atvTagsNorm, tagsFiltroSet);
                    } catch (Exception e) {
                        return false;
                    }
                })
                // termo em categoria/subCategoria
                .filter(a -> term.isBlank()
                        || ActivityFilterParams.normalize(a.getCategoria()).contains(term)
                        || ActivityFilterParams.normalize(a.getSubCategoria()).contains(term))
                .toList();

        // --- 4) Paginação manual usando o Pageable recebido ---
        int total = filtrado.size(); // *** total REAL das atividades filtradas ***
        int pageNumber = pageable.getPageNumber();
        int pageSize   = pageable.getPageSize();

        int fromIndex = (int) pageable.getOffset(); // pageNumber * pageSize
        if (fromIndex > total) {
            fromIndex = total;
        }
        int toIndex = Math.min(fromIndex + pageSize, total);

        var pageContent = filtrado.subList(fromIndex, toIndex);

        // devolve Page<Activity> com total correto
        return new org.springframework.data.domain.PageImpl<>(
                pageContent,
                pageable,
                total
        );
    }

    private java.util.Set<String> toNormalizedSet(java.util.List<String> items) {
        if (items == null) {
            return java.util.Collections.emptySet();
        }
        return items.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .map(ActivityFilterParams::normalize)
                .collect(java.util.stream.Collectors.toSet());
    }
}
