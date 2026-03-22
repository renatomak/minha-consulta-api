package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_equipe")
public class Equipe {

    @Id
    @Column(name = "co_seq_equipe")
    private Long coSeqEquipe;

    @Column(name = "nu_ine")
    private String ine;

    @Column(name = "no_equipe")
    private String nome;

    @Column(name = "co_unidade_saude")
    private Long coUnidadeSaude;

    @Column(name = "st_ativo")
    private Integer ativo;

    public Long getCoSeqEquipe() {
        return coSeqEquipe;
    }

    public String getIne() {
        return ine;
    }

    public String getNome() {
        return nome;
    }

    public Long getCoUnidadeSaude() {
        return coUnidadeSaude;
    }

    public Integer getAtivo() {
        return ativo;
    }
}

