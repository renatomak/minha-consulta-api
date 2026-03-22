package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_situacao_agendado")
public class SituacaoAgendado {

    @Id
    @Column(name = "co_situacao_agendado")
    private Long coSituacaoAgendado;

    @Column(name = "no_situacao_agendado")
    private String descricao;

    @Column(name = "no_identificador")
    private String identificador;

    public Long getCoSituacaoAgendado() {
        return coSituacaoAgendado;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getIdentificador() {
        return identificador;
    }
}

