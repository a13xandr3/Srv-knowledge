package br.com.knowledgebase.adapters.inbound.web.dto;

public record TwoFactorVerifyRequest(
        @jakarta.validation.constraints.NotBlank String username,
        @jakarta.validation.constraints.Pattern(regexp="\\d{6}") String code
) {}
