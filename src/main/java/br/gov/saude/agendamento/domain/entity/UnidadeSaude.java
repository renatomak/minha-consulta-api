package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_unidade_saude")
public class UnidadeSaude {

    @Id
    @Column(name = "co_seq_unidade_saude")
    private Long coSeqUnidadeSaude;

    @Column(name = "nu_cnes")
    private String cnes;

    @Column(name = "no_unidade_saude")
    private String nome;

    @Column(name = "ds_logradouro")
    private String logradouro;

    @Column(name = "nu_numero")
    private String numero;

    @Column(name = "no_bairro")
    private String bairro;

    @Column(name = "ds_cep")
    private String cep;

    @Column(name = "st_ativo")
    private Integer ativo;

    public Long getCoSeqUnidadeSaude() {
        return coSeqUnidadeSaude;
    }

    public String getCnes() {
        return cnes;
    }

    public String getNome() {
        return nome;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCep() {
        return cep;
    }

    public Integer getAtivo() {
        return ativo;
    }
}

