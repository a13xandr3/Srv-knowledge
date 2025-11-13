package br.com.knowledgebase.adapters.outbound.persistence.jpa;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.FileAssetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFileRepository extends JpaRepository<FileAssetJpaEntity, Long> {}