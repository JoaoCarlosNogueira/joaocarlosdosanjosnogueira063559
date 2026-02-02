package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity;

import jakarta.persistence.*;

@Entity
public class Regional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long externalId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean ativo;

    public Regional(){

    }
    public Regional(Long externalId, String nome, boolean ativo) {
        this.externalId = externalId;
        this.nome = nome;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
