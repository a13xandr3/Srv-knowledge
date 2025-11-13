package br.com.knowledgebase.adapters.outbound.persistence.jpa.mapper;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.entity.ActivityJpaEntity;
import br.com.knowledgebase.domain.model.Activity;

public final class ActivityMapper {

    private ActivityMapper() {}

    public static Activity toDomain(ActivityJpaEntity e){
        if (e == null) return null;
        Activity d = new Activity();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setDescricao(e.getDescricao());
        d.setCategoria(e.getCategoria());
        d.setSubCategoria(e.getSubCategoria());
        d.setTag(e.getTag());
        d.setUri(e.getUri());
        d.setFileID(e.getFileID());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        d.setDataEntradaManha(e.getDataEntradaManha());
        d.setDataSaidaManha(e.getDataSaidaManha());
        d.setDataEntradaTarde(e.getDataEntradaTarde());
        d.setDataSaidaTarde(e.getDataSaidaTarde());
        d.setDataEntradaNoite(e.getDataEntradaNoite());
        d.setDataSaidaNoite(e.getDataSaidaNoite());
        return d;
    }

    public static ActivityJpaEntity toJpa(Activity d){
        if (d == null) return null;
        ActivityJpaEntity e = new ActivityJpaEntity();
        e.setId(d.getId());
        e.setName(d.getName());               // mapeia do domínio
        e.setDescricao(d.getDescricao());
        e.setCategoria(d.getCategoria());
        e.setSubCategoria(d.getSubCategoria());
        e.setTag(d.getTag());
        e.setUri(d.getUri());
        e.setFileID(d.getFileID());
        e.setCreatedAt(d.getCreatedAt());         // pode deixar nulo p/ @PrePersist preencher
        e.setUpdatedAt(d.getUpdatedAt());
        e.setDataEntradaManha(d.getDataEntradaManha());
        e.setDataSaidaManha(d.getDataSaidaManha());
        e.setDataEntradaTarde(d.getDataEntradaTarde());
        e.setDataSaidaTarde(d.getDataSaidaTarde());
        e.setDataEntradaNoite(d.getDataEntradaNoite());
        e.setDataSaidaNoite(d.getDataSaidaNoite());
        return e;
    }

}
