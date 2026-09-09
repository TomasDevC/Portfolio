package com.starterkit.springboot.fornecedor;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(generator = "forn-id-gen")
    @GenericGenerator(name = "forn-id-gen", strategy = "increment")
    private Long id;

    @Column(nullable = false, length = 25)
    private String nome;

    @Column(nullable = false, length = 125)
    private String morada;

    @Column(nullable = false, length = 50)
    private String localidade;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 25)
    private String telemovel;

    @Column(nullable = false, length = 50)
    private String site;

    public Long getId() { return id; }


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
