package br.com.knowledgebase.adapters.outbound.persistence.jpa;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.ActivityJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaActivityRepository extends JpaRepository<ActivityJpaEntity, Long> {

    @Query("""
    select e from ActivityJpaEntity e
    where (:term is null or :term = ''
       or lower(e.name)       like lower(concat('%', :term, '%'))
       or lower(e.categoria)    like lower(concat('%', :term, '%'))
       or lower(e.subCategoria) like lower(concat('%', :term, '%'))
       or lower(e.descricao)    like lower(concat('%', :term, '%'))
    )
  """)

    Page<ActivityJpaEntity> search(@Param("term") String term, Pageable pageable);

    @Query("select distinct a.categoria from ActivityJpaEntity a")
    List<String> findDistinctCategorias();

    @Query("select distinct a.tag from ActivityJpaEntity a")
    List<String> findDistinctTags();

}