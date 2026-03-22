package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.dto.response.CidadaoDetalheResponse;
import br.gov.saude.agendamento.exception.CidadaoNaoEncontradoException;
import br.gov.saude.agendamento.repository.CidadaoRepository;
import br.gov.saude.agendamento.repository.CidadaoVinculacaoEquipeRepository;
import br.gov.saude.agendamento.repository.projection.CidadaoCpfProjection;
import br.gov.saude.agendamento.repository.projection.VinculoEquipeProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CidadaoServiceTest {

    @Mock
    private CidadaoRepository cidadaoRepository;
    @Mock
    private CidadaoVinculacaoEquipeRepository vinculacaoEquipeRepository;

    @InjectMocks
    private CidadaoService service;

    @Test
    void buscarPorCpfDeveRetornarDadosCompletos() {
        when(cidadaoRepository.buscarPorCpf("50000800001")).thenReturn(Optional.of(new CidadaoCpfProjection() {
            public Long getCoSeqCidadao() { return 800001L; }
            public String getNome() { return "MARIA"; }
            public String getNuCpf() { return "50000800001"; }
            public String getNuCns() { return "800"; }
            public LocalDate getDtNascimento() { return LocalDate.of(1971, 3, 15); }
            public String getNoSexo() { return "FEMININO"; }
            public String getNuTelefoneCelular() { return "479999"; }
            public String getDsEmail() { return null; }
            public Long getCoProntuario() { return 900001L; }
        }));

        when(vinculacaoEquipeRepository.buscarVinculoMaisRecente(800001L)).thenReturn(Optional.of(new VinculoEquipeProjection() {
            public String getNuCnes() { return "2748304"; }
            public String getNuIne() { return "0000068017"; }
            public Long getCoSeqUnidadeSaude() { return 1001L; }
            public String getNoUnidadeSaude() { return "UBS A"; }
            public String getDsLogradouro() { return "RUA"; }
            public String getNuNumero() { return "250"; }
            public String getNoBairro() { return "CENTRO"; }
            public String getDsCep() { return "89119210"; }
            public Long getCoSeqEquipe() { return 2001L; }
            public String getNoEquipe() { return "ESF I"; }
            public String getIneEquipe() { return "0000068017"; }
        }));

        CidadaoDetalheResponse response = service.buscarPorCpf("50000800001");

        assertEquals(800001L, response.coSeqCidadao());
        assertEquals(900001L, response.coProntuario());
        assertEquals(2001L, response.equipe().coSeq());
    }

    @Test
    void buscarPorCpfInexistenteDeveLancar() {
        when(cidadaoRepository.buscarPorCpf("000")).thenReturn(Optional.empty());
        assertThrows(CidadaoNaoEncontradoException.class, () -> service.buscarPorCpf("000"));
    }
}

