package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.MotivoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotivoReservaRepository extends JpaRepository<MotivoReserva, Long> {
    List<MotivoReserva> findAllByOrderByCoAsc();
}

