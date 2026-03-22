package br.gov.saude.agendamento.repository;

public interface AgendadoRepositoryCustom {
    Long inserirAgendamento(
            Long coLotacao,
            Long coProntuario,
            String data,
            String horaInicio,
            Long coOrigem,
            Long coMotivoReserva,
            String observacao,
            String uuid
    );
}

