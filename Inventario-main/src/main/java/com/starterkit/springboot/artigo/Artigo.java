package com.starterkit.springboot.artigo;

import java.util.UUID;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "artigos")
public class Artigo {

    @Id
    @GeneratedValue(generator = "artigo-id-gen")
    @GenericGenerator(name = "artigo-id-gen", strategy = "increment")
    private Long id;

    private Integer consumoWatts;
    private String tipoLigacao;
    private String socketProcessador;
    private Integer slotsRamTotal;
    private String barramentoPrincipal;
    private String materialConstrucao;
    private Boolean suporteWindows11;
    private String layoutTeclado;
    private Integer tdpMaximo;
    private String slotsExpansao;
    private String versaoBluetooth;
    private Boolean cameraPrivacidade;
    
    @Column(columnDefinition = "TEXT")
    private String logsTecnicos;

    @Column(name = "codigo_unico", unique = true, length = 36, updatable = false)
    private String codigoUnico;

    @Column(name = "imagem_path")
    private String imagemPath;

    @PrePersist
    public void ensureCodigoUnico() {
        if (codigoUnico == null || codigoUnico.trim().isEmpty()) {
            codigoUnico = UUID.randomUUID().toString();
        }
    }

    public Artigo() {}

    
    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getConsumoWatts() { return consumoWatts; }
    public void setConsumoWatts(Integer consumoWatts) { this.consumoWatts = consumoWatts; }
    public String getTipoLigacao() { return tipoLigacao; }
    public void setTipoLigacao(String tipoLigacao) { this.tipoLigacao = tipoLigacao; }
    public String getSocketProcessador() { return socketProcessador; }
    public void setSocketProcessador(String socketProcessador) { this.socketProcessador = socketProcessador; }
    public Integer getSlotsRamTotal() { return slotsRamTotal; }
    public void setSlotsRamTotal(Integer slotsRamTotal) { this.slotsRamTotal = slotsRamTotal; }
    public String getBarramentoPrincipal() { return barramentoPrincipal; }
    public void setBarramentoPrincipal(String barramentoPrincipal) { this.barramentoPrincipal = barramentoPrincipal; }
    public String getMaterialConstrucao() { return materialConstrucao; }
    public void setMaterialConstrucao(String materialConstrucao) { this.materialConstrucao = materialConstrucao; }
    public Boolean getSuporteWindows11() { return suporteWindows11; }
    public void setSuporteWindows11(Boolean suporteWindows11) { this.suporteWindows11 = suporteWindows11; }
    public String getLayoutTeclado() { return layoutTeclado; }
    public void setLayoutTeclado(String layoutTeclado) { this.layoutTeclado = layoutTeclado; }
    public Integer getTdpMaximo() { return tdpMaximo; }
    public void setTdpMaximo(Integer tdpMaximo) { this.tdpMaximo = tdpMaximo; }
    public String getSlotsExpansao() { return slotsExpansao; }
    public void setSlotsExpansao(String slotsExpansao) { this.slotsExpansao = slotsExpansao; }
    public String getVersaoBluetooth() { return versaoBluetooth; }
    public void setVersaoBluetooth(String versaoBluetooth) { this.versaoBluetooth = versaoBluetooth; }
    public Boolean getCameraPrivacidade() { return cameraPrivacidade; }
    public void setCameraPrivacidade(Boolean cameraPrivacidade) { this.cameraPrivacidade = cameraPrivacidade; }
    public String getLogsTecnicos() { return logsTecnicos; }
    public void setLogsTecnicos(String logsTecnicos) { this.logsTecnicos = logsTecnicos; }
    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }
    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }
}