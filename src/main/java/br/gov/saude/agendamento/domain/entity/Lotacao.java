package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_lotacao")
public class Lotacao {

    @Id
    @Column(name = "co_ator_papel")
    private Long coAtorPapel;

    @Column(name = "co_prof")
    private Long coProf;

    @Column(name = "co_cbo")
    private Long coCbo;

    @Column(name = "co_equipe")
    private Long coEquipe;

    @Column(name = "co_unidade_saude")
    private Long coUnidadeSaude;

    @Column(name = "dt_desativacao_lotacao")
    private LocalDateTime dataDesativacaoLotacao;

    public Long getCoAtorPapel() {
        return coAtorPapel;
    }

    public Long getCoProf() {
        return coProf;
    }

    public Long getCoCbo() {
        return coCbo;
    }

    public Long getCoEquipe() {
        return coEquipe;
    }

    public Long getCoUnidadeSaude() {
        return coUnidadeSaude;
    }

    public LocalDateTime getDataDesativacaoLotacao() {
        return dataDesativacaoLotacao;
    }
}

