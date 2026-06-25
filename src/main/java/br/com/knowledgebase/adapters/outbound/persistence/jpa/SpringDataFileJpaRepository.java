package br.com.knowledgebase.adapters.outbound.persistence.jpa;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.FileAssetJpaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataFileJpaRepository extends JpaRepository<FileAssetJpaEntity, Long> {
    @Query("""
            select new br.com.knowledgebase.adapters.outbound.persistence.jpa.FileSnapshotRow(
                f.id,
                f.filename,
                f.mimeType,
                f.contentEncoding,
                f.gzipSizeBytes,
                f.originalSizeBytes,
                f.sha256Hex
            )
            from FileAssetJpaEntity f
            where f.id = :id
            """)
    Optional<FileSnapshotRow> findSnapshotRowById(@Param("id") Long id);

    @Query("""
            select new br.com.knowledgebase.adapters.outbound.persistence.jpa.FileSnapshotRow(
                f.id,
                f.filename,
                f.mimeType,
                f.contentEncoding,
                f.gzipSizeBytes,
                f.originalSizeBytes,
                f.sha256Hex
            )
            from FileAssetJpaEntity f
            where f.id in :ids
            """)
    List<FileSnapshotRow> findSnapshotRowsByIds(@Param("ids") List<Long> ids);
}
