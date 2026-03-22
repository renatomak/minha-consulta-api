package br.gov.saude.agendamento.dto.response;

public record CancelamentoResponse(
        Long coAgendado,
        String status,
        String mensagem
) {
}

