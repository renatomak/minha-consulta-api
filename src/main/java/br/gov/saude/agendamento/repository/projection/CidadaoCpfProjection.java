package br.gov.saude.agendamento.repository.projection;

import java.time.LocalDate;

public interface CidadaoCpfProjection {
    Long getCoSeqCidadao();

    String getNome();

    String getNuCpf();

    String getNuCns();

    LocalDate getDtNascimento();

    String getNoSexo();

    String getNuTelefoneCelular();

    String getDsEmail();

    Long getCoProntuario();
}

