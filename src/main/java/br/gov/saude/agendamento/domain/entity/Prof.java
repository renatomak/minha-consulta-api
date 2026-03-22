package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_prof")
public class Prof {

    @Id
    @Column(name = "co_seq_prof")
    private Long coSeqProf;

    @Column(name = "no_civil_profissional")
    private String nomeCivil;

    @Column(name = "no_social_profissional")
    private String nomeSocial;

    @Column(name = "nu_cpf")
    private String cpf;

    @Column(name = "nu_cns")
    private String cns;

    @Column(name = "no_sexo")
    private String sexo;

    public Long getCoSeqProf() {
        return coSeqProf;
    }

    public String getNomeCivil() {
        return nomeCivil;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCns() {
        return cns;
    }

    public String getSexo() {
        return sexo;
    }
}

