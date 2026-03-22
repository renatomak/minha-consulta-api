package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.ConfigAgendaFechamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConfigAgendaFechamentoRepository extends JpaRepository<ConfigAgendaFechamento, Long> {

    @Query(value = """
            SELECT dt_inicio, dt_fim, ds_motivo, no_ident_motivo_fechamento, co_seq_config_agenda_fechament,
                   co_lotacao, st_registro_ativo
            FROM public.tb_config_agenda_fechamento
            WHERE co_lotacao = :coLotacao
              AND st_registro_ativo = 1
              AND dt_fim >= :dataInicio
              AND dt_inicio <= :dataFim
            ORDER BY dt_inicio
            """, nativeQuery = true)
    List<ConfigAgendaFechamento> buscarFechamentos(Long coLotacao, LocalDateTime dataInicio, LocalDateTime dataFim);
}

