package br.gov.saude.agendamento.dto.response;

public record ProfissionalResponse(
        Long coLotacao,
        Long coProf,
        String nome,
        String cbo,
        Long coCbo,
        boolean hasAgenda
) {
}

