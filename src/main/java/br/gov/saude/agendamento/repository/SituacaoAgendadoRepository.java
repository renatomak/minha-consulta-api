package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.SituacaoAgendado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SituacaoAgendadoRepository extends JpaRepository<SituacaoAgendado, Long> {
    List<SituacaoAgendado> findAllByOrderByCoSituacaoAgendadoAsc();

    Optional<SituacaoAgendado> findByIdentificador(String identificador);
}

