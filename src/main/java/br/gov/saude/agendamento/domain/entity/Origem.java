package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_origem")
public class Origem {

    @Id
    @Column(name = "co_origem")
    private Long co;

    @Column(name = "ds_origem")
    private String descricao;

    public Long getCo() {
        return co;
    }

    public String getDescricao() {
        return descricao;
    }
}

