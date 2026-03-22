package br.gov.saude.agendamento.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_motivo_reserva")
public class MotivoReserva {

    @Id
    @Column(name = "co_motivo_reserva")
    private Long co;

    @Column(name = "no_motivo_reserva")
    private String descricao;

    public Long getCo() {
        return co;
    }

    public String getDescricao() {
        return descricao;
    }
}

