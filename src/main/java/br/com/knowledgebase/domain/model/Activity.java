package br.com.knowledgebase.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

public class Activity {

    private Long id;
    private String name;
    private String descricao;
    private String categoria;
    private String subCategoria;
    private Map<String, Object> tag;
    private Map<String, Object> uri;
    private Map<String, Object> fileID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dataEntradaManha;
    private LocalDateTime dataSaidaManha;
    private LocalDateTime dataEntradaTarde;
    private LocalDateTime dataSaidaTarde;
    private LocalDateTime dataEntradaNoite;
    private LocalDateTime dataSaidaNoite;

    public Activity() {}
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

    /**
     * Atualiza os campos editáveis desta Activity a partir de outra instância.
     * Não altera createdAt. Define updatedAt com 'now' (ou instante atual se null).
     */
    public void updateFrom(Activity src, LocalDateTime now) {
        if (src == null) return;
        setName(src.getName());
        setDescricao(src.getDescricao());
        setCategoria(src.getCategoria());
        setSubCategoria(src.getSubCategoria()); // atenção: copia a subcategoria correta
        setTag(src.getTag());
        setUri(src.getUri());
        setFileID(src.getFileID());
        setDataEntradaManha(src.getDataEntradaManha());
        setDataSaidaManha(src.getDataSaidaManha());
        setDataEntradaTarde(src.getDataEntradaTarde());
        setDataSaidaTarde(src.getDataSaidaTarde());
        setDataEntradaNoite(src.getDataEntradaNoite());
        setDataSaidaNoite(src.getDataSaidaNoite());
        setUpdatedAt(now != null ? now : java.time.LocalDateTime.now());
    }

    public void updateFrom(Activity src) {
        updateFrom(src, null);
    }

}
