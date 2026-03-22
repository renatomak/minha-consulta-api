package br.gov.saude.agendamento.repository.projection;

public interface VinculoEquipeProjection {
    String getNuCnes();

    String getNuIne();

    Long getCoSeqUnidadeSaude();

    String getNoUnidadeSaude();

    String getDsLogradouro();

    String getNuNumero();

    String getNoBairro();

    String getDsCep();

    Long getCoSeqEquipe();

    String getNoEquipe();

    String getIneEquipe();
}

