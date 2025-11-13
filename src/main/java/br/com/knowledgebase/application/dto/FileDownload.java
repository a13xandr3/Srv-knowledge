package br.com.knowledgebase.application.dto;

import org.springframework.core.io.Resource;

public record FileDownload(
        Resource resource,
        String filename,
        String contentType,
        long size
) {}