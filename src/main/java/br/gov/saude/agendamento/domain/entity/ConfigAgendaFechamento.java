package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_config_agenda_fechamento")
public class ConfigAgendaFechamento {

    @Id
    @Column(name = "co_seq_config_agenda_fechament")
    private Long coSeq;

    @Column(name = "co_lotacao")
    private Long coLotacao;

    @Column(name = "dt_inicio")
    private LocalDateTime dtInicio;

    @Column(name = "dt_fim")
    private LocalDateTime dtFim;

    @Column(name = "ds_motivo")
    private String motivo;

    @Column(name = "no_ident_motivo_fechamento")
    private String identificadorMotivo;

    @Column(name = "st_registro_ativo")
    private Integer ativo;

    public Long getCoSeq() {
        return coSeq;
    }

    public Long getCoLotacao() {
        return coLotacao;
    }

    public LocalDateTime getDtInicio() {
        return dtInicio;
    }

    public LocalDateTime getDtFim() {
        return dtFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getIdentificadorMotivo() {
        return identificadorMotivo;
    }

    public Integer getAtivo() {
        return ativo;
    }
}

