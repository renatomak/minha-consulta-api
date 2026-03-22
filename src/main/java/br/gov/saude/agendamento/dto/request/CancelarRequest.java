package br.gov.saude.agendamento.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CancelarRequest(
        @NotBlank
        @Pattern(regexp = "CIDADAO|PROFISSIONAL", message = "motivoCancelamento deve ser CIDADAO ou PROFISSIONAL")
        String motivoCancelamento,
        String observacao
) {
}

