package br.gov.saude.agendamento.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AgendadoRepositoryImpl implements AgendadoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long inserirAgendamento(
            Long coLotacao,
            Long coProntuario,
            String data,
            String horaInicio,
            Long coOrigem,
            Long coMotivoReserva,
            String observacao,
            String uuid
    ) {
        Object result = entityManager.createNativeQuery("""
                INSERT INTO public.tb_agendado (
                    dt_agendado, hr_inicial_agendado, ds_observacao,
                    st_agendado, co_lotacao_agendada, co_motivo_reserva,
                    co_origem, co_prontuario, co_ator_papel_criador,
                    dt_criacao, uuid_agendamento, st_sincronizacao,
                    st_fora_ubs, tp_agendamento,
                    st_suprime_notificacao_agonl, st_enviou_email_cidadao
                ) VALUES (
                    CAST(:data AS date),
                    CAST(:data || ' ' || :horaInicio AS timestamp),
                    :observacao,
                    1,
                    :coLotacao,
                    :coMotivoReserva,
                    :coOrigem,
                    :coProntuario,
                    :coLotacao,
                    NOW(),
                    :uuid,
                    'NAO_SINCRONIZADO',
                    0, NULL, 0, 0
                )
                RETURNING co_seq_agendado
                """)
                .setParameter("data", data)
                .setParameter("horaInicio", horaInicio)
                .setParameter("observacao", observacao)
                .setParameter("coLotacao", coLotacao)
                .setParameter("coMotivoReserva", coMotivoReserva)
                .setParameter("coOrigem", coOrigem)
                .setParameter("coProntuario", coProntuario)
                .setParameter("uuid", uuid)
                .getSingleResult();
        return ((Number) result).longValue();
    }
}

