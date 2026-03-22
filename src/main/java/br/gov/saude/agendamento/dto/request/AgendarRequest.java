package br.gov.saude.agendamento.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendarRequest(
        @NotNull Long coLotacao,
        @NotNull Long coProntuario,
        @NotNull LocalDate data,
        @NotNull LocalTime horaInicio,
        @NotNull Long coOrigem,
        Long coMotivoReserva,
        String observacao
) {
}

