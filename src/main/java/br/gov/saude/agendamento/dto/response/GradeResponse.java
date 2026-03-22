package br.gov.saude.agendamento.dto.response;

import java.time.LocalDate;
import java.util.List;

public record GradeResponse(
        Long coLotacao,
        String profissional,
        String cbo,
        LocalDate data,
        String diaSemana,
        boolean bloqueado,
        String motivoBloqueio,
        String mensagem,
        List<SlotResponse> slots,
        int totalSlots,
        int slotsDisponiveis,
        int slotsOcupados,
        int slotsBloqueados
) {
}

