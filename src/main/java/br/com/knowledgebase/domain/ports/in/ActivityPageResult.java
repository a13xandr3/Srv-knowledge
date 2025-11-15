package br.com.knowledgebase.domain.ports.in;

import br.com.knowledgebase.domain.model.Activity;

import java.util.List;

public record ActivityPageResult(
        List<Activity> activities,
        int total,
        int page,
        int size
) {}