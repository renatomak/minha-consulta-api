package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_prontuario")
public class Prontuario {

    @Id
    @Column(name = "co_seq_prontuario")
    private Long coSeqProntuario;

    @Column(name = "co_cidadao")
    private Long coCidadao;

    public Long getCoSeqProntuario() {
        return coSeqProntuario;
    }

    public Long getCoCidadao() {
        return coCidadao;
    }
}

