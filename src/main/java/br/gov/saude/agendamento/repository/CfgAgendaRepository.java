package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.CfgAgenda;
import br.gov.saude.agendamento.repository.projection.AgendaConfigProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CfgAgendaRepository extends JpaRepository<CfgAgenda, Long> {

    boolean existsByCoEntidadeConfigurada(Long coEntidadeConfigurada);

    @Query(value = """
            SELECT
                ds.no_identificador AS dia_semana,
                per.no_identificador AS periodo,
                cad.horario_inicial, cad.horario_final
            FROM public.tb_cfg_agenda ca
            JOIN public.tb_cfg_agenda_detalhe cad ON cad.co_cfg_agenda = ca.co_seq_config_agenda
            JOIN public.tb_dia_semana ds ON ds.co_dia_semana = cad.co_dia_semana
            JOIN public.tb_periodo per   ON per.co_periodo   = cad.co_periodo
            WHERE ca.co_entidade_configurada = :coLotacao
            ORDER BY cad.co_dia_semana, cad.co_periodo
            """, nativeQuery = true)
    List<AgendaConfigProjection> buscarConfiguracao(Long coLotacao);
}

