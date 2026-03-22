package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {
}

