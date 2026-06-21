package br.com.knowledgebase.domain.ports.in;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityFilterParamsTest {

    @Test
    void normalizeKeepsComposedTermComparable() {
        assertThat(ActivityFilterParams.normalize("Home Office"))
                .isEqualTo("home office");
    }

    @Test
    void normalizeCollapsesWhitespaceVariations() {
        assertThat(ActivityFilterParams.normalize("  Home\u00A0  Office  "))
                .isEqualTo("home office");
    }
}
