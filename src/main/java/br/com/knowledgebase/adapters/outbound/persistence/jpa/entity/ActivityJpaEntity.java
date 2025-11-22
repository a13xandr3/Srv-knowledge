package br.com.knowledgebase.adapters.outbound.persistence.jpa.entity;

import br.com.knowledgebase.adapters.outbound.persistence.jpa.converter.JsonMapConverter;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "link", indexes = {
        @Index(name="idx_atividade_categoria", columnList = "categoria"),
        @Index(name="idx_atividade_createdAt", columnList = "createdAt")
})
public class ActivityJpaEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "subCategoria", length = 100)
    private String subCategoria;

    @Column(name = "tag", columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> tag;

    @Column(name = "uri", columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> uri;

    @Column(name = "fileID", columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> fileID;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "dataEntradaManha")
    private LocalDateTime dataEntradaManha;

    @Column(name = "dataSaidaManha")
    private LocalDateTime dataSaidaManha;

    @Column(name = "dataEntradaTarde")
    private LocalDateTime dataEntradaTarde;

    @Column(name = "dataSaidaTarde")
    private LocalDateTime dataSaidaTarde;

    @Column(name = "dataEntradaNoite")
    private LocalDateTime dataEntradaNoite;

    @Column(name = "dataSaidaNoite")
    private LocalDateTime dataSaidaNoite;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getSubCategoria() { return subCategoria; }
    public void setSubCategoria(String subCategoria) { this.subCategoria = subCategoria; }

    public Map<String, Object> getTag() { return tag; }
    public void setTag(Map<String, Object> tag) { this.tag = tag; }

    public Map<String, Object> getUri() { return uri; }
    public void setUri(Map<String, Object> uri) { this.uri = uri; }

    public Map<String, Object> getFileID() { return fileID; }
    public void setFileID(Map<String, Object> fileID) { this.fileID = fileID; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDataEntradaManha() { return dataEntradaManha; }
    public void setDataEntradaManha(LocalDateTime v) { this.dataEntradaManha = v; }

    public LocalDateTime getDataSaidaManha() { return dataSaidaManha; }
    public void setDataSaidaManha(LocalDateTime v) { this.dataSaidaManha = v; }

    public LocalDateTime getDataEntradaTarde() { return dataEntradaTarde; }
    public void setDataEntradaTarde(LocalDateTime v) { this.dataEntradaTarde = v; }

    public LocalDateTime getDataSaidaTarde() { return dataSaidaTarde; }
    public void setDataSaidaTarde(LocalDateTime v) { this.dataSaidaTarde = v; }

    public LocalDateTime getDataEntradaNoite() { return dataEntradaNoite; }
    public void setDataEntradaNoite(LocalDateTime v) { this.dataEntradaNoite = v; }

    public LocalDateTime getDataSaidaNoite() { return dataSaidaNoite; }
    public void setDataSaidaNoite(LocalDateTime v) { this.dataSaidaNoite = v; }
}
