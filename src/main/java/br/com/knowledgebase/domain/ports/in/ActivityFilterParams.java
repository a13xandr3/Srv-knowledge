package br.com.knowledgebase.domain.ports.in;

import java.util.List;

public record ActivityFilterParams(
        List<String> excessao,
        List<String> categoria,
        List<String> tag,
        String categoriaTerm
) {}