package com.starterkit.springboot.equipamento;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

public class EquipamentoRequest {

    @NotBlank
    @Size(max = 25)
    private String categoria;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCompra;

    @NotBlank
    @Size(max = 25)
    private String modelo;

    @NotBlank
    @Size(max = 25)
    private String marca;

    private Long numeroSerie;

    @NotBlank
    @Size(max = 25)
    private String local;

    private Boolean garantia = Boolean.TRUE;

    private Boolean seguro = Boolean.FALSE;

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Long getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(Long numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Boolean getGarantia() {
        return garantia;
    }

    public void setGarantia(Boolean garantia) {
        this.garantia = garantia;
    }

    public Boolean getSeguro() {
        return seguro;
    }

    public void setSeguro(Boolean seguro) {
        this.seguro = seguro;
    }
}
