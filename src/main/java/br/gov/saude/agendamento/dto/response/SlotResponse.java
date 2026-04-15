package br.gov.saude.agendamento.dto.response;

public record SlotResponse(
        String horaInicio,
        String horaFim,
        String periodo,
        String situacao,
        Long coAgendado,
        Long prontuario,
        String statusAgendamento,
        String paciente
) {
}

