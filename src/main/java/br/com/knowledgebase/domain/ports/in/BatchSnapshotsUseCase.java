package br.com.knowledgebase.domain.ports.in;

import br.com.knowledgebase.domain.model.Snapshot;

import java.util.List;

public interface BatchSnapshotsUseCase {
    List<Snapshot> getAllPreservingOrder(java.util.List<Long> ids, boolean includeBase64);
}
