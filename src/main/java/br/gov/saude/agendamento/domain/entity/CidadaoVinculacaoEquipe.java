package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_cidadao_vinculacao_equipe")
public class CidadaoVinculacaoEquipe {

    @Id
    @Column(name = "co_seq_cidadao_vinculacao_eqp")
    private Long coSeq;

    @Column(name = "co_cidadao")
    private Long coCidadao;

    @Column(name = "nu_cnes")
    private String cnes;

    @Column(name = "nu_ine")
    private String ine;

    @Column(name = "dt_atualizacao_cadastro")
    private OffsetDateTime dataAtualizacao;

    public Long getCoSeq() {
        return coSeq;
    }

    public Long getCoCidadao() {
        return coCidadao;
    }

    public String getCnes() {
        return cnes;
    }

    public String getIne() {
        return ine;
    }

    public OffsetDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}

