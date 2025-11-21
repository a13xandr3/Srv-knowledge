package br.com.knowledgebase.adapters.inbound.web.dto;

import java.util.List;

public record IdsRequest(
        List<Long> ids,
        Boolean includeBase64
) {}