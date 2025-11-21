package br.com.knowledgebase.domain.ports.in;

import br.com.knowledgebase.domain.model.Snapshot;

public interface GetSnapshotUseCase {
    Snapshot get(Long id, boolean includeBase64);
}