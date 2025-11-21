package br.com.knowledgebase.adapters.inbound.web.dto;

import br.com.knowledgebase.domain.model.Activity;
import java.util.List;

public record ActivityPageResponse(
        List<Activity> atividades,
        long total,
        int page,
        int limit
) {}
