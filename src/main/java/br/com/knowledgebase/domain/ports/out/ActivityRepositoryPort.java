package br.com.knowledgebase.domain.ports.out;

import br.com.knowledgebase.domain.model.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityRepositoryPort {
    Activity save(Activity a);
    Optional<Activity> findById(Long id);
    void deleteById(Long id);
    List<Activity> list(int page, int size);
    List<Activity> search(String term, int page, int size);
    /**
     * Retorna todas as categorias distintas existentes na tabela/link/Activity.
     * A infraestrutura decide a melhor forma de implementar (JPQL, SQL nativo, etc.).
     */
    List<String> findDistinctCategorias();
}