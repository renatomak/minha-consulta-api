package br.gov.saude.agendamento.repository.projection;

public interface GradeSlotProjection {
    String getHorarioInicio();

    String getHorarioFim();

    String getPeriodo();

    String getSituacao();

    Long getCoSeqAgendado();

    String getStatusAgendamento();

    String getPaciente();

    String getMotivoBloqueio();
}

