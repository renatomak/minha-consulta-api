package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "tb_cidadao")
public class Cidadao {

    @Id
    @Column(name = "co_seq_cidadao")
    private Long coSeqCidadao;

    @Column(name = "nu_cpf")
    private String cpf;

    @Column(name = "nu_cns")
    private String cns;

    @Column(name = "no_cidadao")
    private String nomeCidadao;

    @Column(name = "no_social")
    private String nomeSocial;

    @Column(name = "dt_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "no_sexo")
    private String sexo;

    @Column(name = "nu_telefone_celular")
    private String telefone;

    @Column(name = "ds_email")
    private String email;

    @Column(name = "st_ativo")
    private Integer ativo;

    public Long getCoSeqCidadao() {
        return coSeqCidadao;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCns() {
        return cns;
    }

    public String getNomeCidadao() {
        return nomeCidadao;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAtivo() {
        return ativo;
    }
}

