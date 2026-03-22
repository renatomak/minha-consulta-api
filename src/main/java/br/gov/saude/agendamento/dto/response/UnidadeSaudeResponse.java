package br.gov.saude.agendamento.dto.response;

public record UnidadeSaudeResponse(
        Long coSeq,
        String cnes,
        String nome,
        String endereco,
        String cep
) {
}

