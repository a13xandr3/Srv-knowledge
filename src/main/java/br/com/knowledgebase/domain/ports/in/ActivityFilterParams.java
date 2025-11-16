package br.com.knowledgebase.domain.ports.in;

import java.util.List;
import java.text.Normalizer;
import java.util.Locale;

public record ActivityFilterParams(
        List<String> excessao,
        List<String> categoria,
        List<String> tag,
        String categoriaTerm
) {
    /**
     * Normaliza strings de categoria para comparação:
     * - null -> ""
     * - trim
     * - lowercase (pt-BR)
     * - remove acentos
     */
    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.strip().toLowerCase(new Locale("pt", "BR"));
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

}