package br.gov.saude.agendamento.dto.response;

import java.time.LocalDate;

public record AgendamentoResponse(
        Long coAgendado,
        LocalDate data,
        String horaInicio,
        String status,
        String motivo,
        String paciente,
        String cpfPaciente,
        String telefonePaciente,
        String observacao
) {
}

