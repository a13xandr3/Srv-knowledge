package br.com.knowledgebase.domain.ports.in;

import br.com.knowledgebase.domain.model.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityUseCase {
    Activity create(Activity a);
    Optional<Activity> get(Long id);
    Activity update(Long id, Activity a);
    void delete(Long id);
    List<Activity> list(int page, int size);
    List<Activity> search(String term, int page, int size);
    ActivityPageResult listWithFilters(ActivityFilterParams filterParams, int page, int size);
}