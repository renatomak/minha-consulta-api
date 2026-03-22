package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.Origem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrigemRepository extends JpaRepository<Origem, Long> {
    List<Origem> findAllByOrderByCoAsc();
}

