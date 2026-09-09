package com.starterkit.springboot.fornecedor;

import javax.persistence.Column;

public class FornecedorRequest {
  
    private String nome;
    private String morada;
    private String localidade;
    private String email;
    private String telemovel;
    private String site;

     public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
}