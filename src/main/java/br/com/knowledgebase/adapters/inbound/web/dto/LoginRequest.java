package br.com.knowledgebase.adapters.inbound.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password,
        String totp
    ) {}
