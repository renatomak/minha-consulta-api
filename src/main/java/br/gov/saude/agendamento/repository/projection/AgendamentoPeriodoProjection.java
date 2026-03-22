package br.gov.saude.agendamento.repository.projection;

import java.time.LocalDate;

public interface AgendamentoPeriodoProjection {
    Long getCoSeqAgendado();

    LocalDate getData();

    String getHoraInicio();

    String getStatus();

    String getStatusId();

    String getMotivo();

    String getDsObservacao();

    String getPaciente();

    String getNuCpf();

    String getNuTelefoneCelular();
}

