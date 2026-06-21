package br.com.knowledgebase.adapters.inbound.web;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ActivitiesControllerTest {

    @Test
    void normalizeExpressionParamsKeepsSpacesInsideSingleExpression() {
        List<String> result = ActivitiesController.normalizeExpressionParams(
                List.of("Banco de Dados", "Machine Learning")
        );

        assertThat(result).containsExactly("Banco de Dados", "Machine Learning");
    }

    @Test
    void normalizeExpressionParamsSplitsOnlyExplicitSeparators() {
        List<String> result = ActivitiesController.normalizeExpressionParams(
                List.of("Banco de Dados, Machine Learning; Cloud Native")
        );

        assertThat(result).containsExactly("Banco de Dados", "Machine Learning", "Cloud Native");
    }

    @Test
    void normalizeExpressionParamsRespectsQuotedSeparators() {
        List<String> result = ActivitiesController.normalizeExpressionParams(
                List.of("\"Banco, Dados\"; 'Machine; Learning'")
        );

        assertThat(result).containsExactly("Banco, Dados", "Machine; Learning");
    }
}
