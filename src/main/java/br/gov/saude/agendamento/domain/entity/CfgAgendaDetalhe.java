package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cfg_agenda_detalhe")
public class CfgAgendaDetalhe {

    @Id
    @Column(name = "co_seq_config_agenda_detalhe")
    private Long coSeqConfigAgendaDetalhe;

    @Column(name = "co_cfg_agenda")
    private Long coCfgAgenda;

    @Column(name = "co_dia_semana")
    private Long coDiaSemana;

    @Column(name = "co_periodo")
    private Long coPeriodo;

    @Column(name = "horario_inicial")
    private String horarioInicial;

    @Column(name = "horario_final")
    private String horarioFinal;

    public Long getCoSeqConfigAgendaDetalhe() {
        return coSeqConfigAgendaDetalhe;
    }

    public Long getCoCfgAgenda() {
        return coCfgAgenda;
    }

    public Long getCoDiaSemana() {
        return coDiaSemana;
    }

    public Long getCoPeriodo() {
        return coPeriodo;
    }

    public String getHorarioInicial() {
        return horarioInicial;
    }

    public String getHorarioFinal() {
        return horarioFinal;
    }
}

