package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cfg_agenda")
public class CfgAgenda {

    @Id
    @Column(name = "co_seq_config_agenda")
    private Long coSeqConfigAgenda;

    @Column(name = "co_entidade_configurada")
    private Long coEntidadeConfigurada;

    @Column(name = "co_tipo_cfg_agenda")
    private Long coTipoCfgAgenda;

    public Long getCoSeqConfigAgenda() {
        return coSeqConfigAgenda;
    }

    public Long getCoEntidadeConfigurada() {
        return coEntidadeConfigurada;
    }

    public Long getCoTipoCfgAgenda() {
        return coTipoCfgAgenda;
    }
}

