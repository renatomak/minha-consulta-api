package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_agendado")
public class Agendado {

    @Id
    @Column(name = "co_seq_agendado")
    private Long coSeqAgendado;

    @Column(name = "dt_agendado")
    private LocalDateTime dtAgendado;

    @Column(name = "hr_inicial_agendado")
    private LocalDateTime hrInicialAgendado;

    @Column(name = "ds_observacao")
    private String observacao;

    @Column(name = "st_agendado")
    private Long stAgendado;

    @Column(name = "co_lotacao_agendada")
    private Long coLotacaoAgendada;

    @Column(name = "co_motivo_reserva")
    private Long coMotivoReserva;

    @Column(name = "co_origem")
    private Long coOrigem;

    @Column(name = "co_prontuario")
    private Long coProntuario;

    @Column(name = "co_ator_papel_criador")
    private Long coAtorPapelCriador;

    @Column(name = "dt_criacao")
    private OffsetDateTime dtCriacao;

    @Column(name = "uuid_agendamento")
    private String uuidAgendamento;

    public Long getCoSeqAgendado() {
        return coSeqAgendado;
    }

    public Long getStAgendado() {
        return stAgendado;
    }

    public void setStAgendado(Long stAgendado) {
        this.stAgendado = stAgendado;
    }
}

