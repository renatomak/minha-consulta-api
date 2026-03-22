package br.gov.saude.agendamento.dto.response;

import java.time.LocalDate;

public record CidadaoDetalheResponse(
        Long coSeqCidadao,
        String nome,
        String cpf,
        String cns,
        LocalDate dataNascimento,
        Integer idade,
        String sexo,
        String telefone,
        String email,
        Long coProntuario,
        UnidadeSaudeResponse unidadeSaude,
        EquipeResponse equipe
) {
}

