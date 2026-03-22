package br.gov.saude.agendamento.repository.projection;

public interface ProfissionalEquipeProjection {
    Long getCoLotacao();

    Long getCoSeqProf();

    String getNome();

    String getNoCbo();

    Long getCoCbo();

    Boolean getHasAgenda();
}

