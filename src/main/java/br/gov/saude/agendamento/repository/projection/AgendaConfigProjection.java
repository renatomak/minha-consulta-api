package br.gov.saude.agendamento.repository.projection;

public interface AgendaConfigProjection {
    String getDiaSemana();

    String getPeriodo();

    String getHorarioInicial();

    String getHorarioFinal();
}

