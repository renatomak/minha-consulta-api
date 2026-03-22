package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.Agendado;
import br.gov.saude.agendamento.repository.projection.AgendamentoPeriodoProjection;
import br.gov.saude.agendamento.repository.projection.GradeSlotProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AgendadoRepository extends JpaRepository<Agendado, Long>, AgendadoRepositoryCustom {

    @Query(value = """
            SELECT
                ag.co_seq_agendado,
                ag.dt_agendado::date AS data,
                TO_CHAR(ag.hr_inicial_agendado, 'HH24:MI') AS hora_inicio,
                sa.no_situacao_agendado AS status,
                sa.no_identificador AS status_id,
                mo.no_motivo_reserva AS motivo,
                ag.ds_observacao,
                COALESCE(c.no_social, c.no_cidadao) AS paciente,
                c.nu_cpf, c.nu_telefone_celular
            FROM public.tb_agendado ag
            JOIN public.tb_situacao_agendado sa ON sa.co_situacao_agendado = ag.st_agendado
            LEFT JOIN public.tb_motivo_reserva mo ON mo.co_motivo_reserva = ag.co_motivo_reserva
            LEFT JOIN public.tb_prontuario pr ON pr.co_seq_prontuario = ag.co_prontuario
            LEFT JOIN public.tb_cidadao c ON c.co_seq_cidadao = pr.co_cidadao
            WHERE ag.co_lotacao_agendada = :coLotacao
              AND ag.dt_agendado::date BETWEEN :dataInicio AND :dataFim
            ORDER BY ag.dt_agendado, ag.hr_inicial_agendado
            """, nativeQuery = true)
    List<AgendamentoPeriodoProjection> listarAgendamentosPeriodo(Long coLotacao, LocalDate dataInicio, LocalDate dataFim);

    @Query(value = """
            WITH slots AS (
                SELECT
                    TO_CHAR(
                        cad.horario_inicial::time + (n * INTERVAL '20 minutes'),
                        'HH24:MI'
                    ) AS horario,
                    TO_CHAR(
                        cad.horario_inicial::time + ((n + 1) * INTERVAL '20 minutes'),
                        'HH24:MI'
                    ) AS horario_fim,
                    per.no_identificador AS periodo
                FROM public.tb_cfg_agenda ca
                JOIN public.tb_cfg_agenda_detalhe cad ON cad.co_cfg_agenda = ca.co_seq_config_agenda
                JOIN public.tb_periodo per ON per.co_periodo = cad.co_periodo
                CROSS JOIN generate_series(0,
                    (EXTRACT(EPOCH FROM (cad.horario_final::time - cad.horario_inicial::time)) / 1200)::INT - 1
                ) AS n
                WHERE ca.co_entidade_configurada = :coLotacao
                  AND cad.co_dia_semana = :diaSemana
            ),
            ocupados AS (
                SELECT
                    TO_CHAR(ag.hr_inicial_agendado, 'HH24:MI') AS horario,
                    ag.co_seq_agendado,
                    sa.no_identificador AS status,
                    COALESCE(c.no_social, c.no_cidadao) AS paciente
                FROM public.tb_agendado ag
                JOIN public.tb_situacao_agendado sa ON sa.co_situacao_agendado = ag.st_agendado
                LEFT JOIN public.tb_prontuario pr ON pr.co_seq_prontuario = ag.co_prontuario
                LEFT JOIN public.tb_cidadao c ON c.co_seq_cidadao = pr.co_cidadao
                WHERE ag.co_lotacao_agendada = :coLotacao
                  AND ag.dt_agendado::date = CAST(:data AS date)
                  AND sa.no_identificador NOT IN (
                      'CANCELADO_CIDADAO', 'CANCELADO_PROFISSIONAL'
                  )
            ),
            bloqueado AS (
                SELECT 1 AS flag, ds_motivo
                FROM public.tb_config_agenda_fechamento
                WHERE co_lotacao = :coLotacao
                  AND st_registro_ativo = 1
                  AND CAST(:data AS date) BETWEEN dt_inicio::date AND dt_fim::date
                LIMIT 1
            )
            SELECT
                s.horario    AS horario_inicio,
                s.horario_fim,
                s.periodo,
                CASE
                    WHEN b.flag = 1            THEN 'BLOQUEADO'
                    WHEN o.horario IS NOT NULL THEN 'OCUPADO'
                    ELSE                            'DISPONIVEL'
                END                           AS situacao,
                o.co_seq_agendado,
                o.status                      AS status_agendamento,
                o.paciente,
                b.ds_motivo                   AS motivo_bloqueio
            FROM slots s
            LEFT JOIN ocupados  o ON o.horario = s.horario
            LEFT JOIN bloqueado b ON TRUE
            ORDER BY s.horario
            """, nativeQuery = true)
    List<GradeSlotProjection> buscarGrade(Long coLotacao, String data, Integer diaSemana);

    @Query(value = """
            SELECT COUNT(1)
            FROM public.tb_agendado ag
            WHERE ag.co_lotacao_agendada = :coLotacao
              AND ag.co_prontuario = :coProntuario
              AND ag.dt_agendado::date = :data
              AND ag.st_agendado = 1
            """, nativeQuery = true)
    long contarAgendamentoAtivoMesmoDia(Long coLotacao, Long coProntuario, LocalDate data);
}

