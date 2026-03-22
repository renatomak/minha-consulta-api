package br.gov.saude.agendamento.dto.response;

import java.time.LocalDate;

public record AgendamentoCriadoResponse(
        Long coAgendado,
        Long coLotacao,
        Long coProntuario,
        LocalDate data,
        String horaInicio,
        String status,
        String uuidAgendamento
) {
}

