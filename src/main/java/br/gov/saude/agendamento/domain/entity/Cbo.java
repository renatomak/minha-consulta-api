package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cbo")
public class Cbo {

    @Id
    @Column(name = "co_cbo")
    private Long coCbo;

    @Column(name = "no_cbo")
    private String nomeCbo;

    public Long getCoCbo() {
        return coCbo;
    }

    public String getNomeCbo() {
        return nomeCbo;
    }
}

