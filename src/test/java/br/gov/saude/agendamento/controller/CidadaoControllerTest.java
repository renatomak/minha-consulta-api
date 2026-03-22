package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.response.CidadaoDetalheResponse;
import br.gov.saude.agendamento.dto.response.EquipeResponse;
import br.gov.saude.agendamento.dto.response.UnidadeSaudeResponse;
import br.gov.saude.agendamento.exception.CidadaoNaoEncontradoException;
import br.gov.saude.agendamento.exception.GlobalExceptionHandler;
import br.gov.saude.agendamento.service.CidadaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CidadaoController.class)
@Import(GlobalExceptionHandler.class)
class CidadaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CidadaoService cidadaoService;

    @Test
    void buscarCidadaoPorCpfValido_deveRetornar200() throws Exception {
        when(cidadaoService.buscarPorCpf("50000800001")).thenReturn(new CidadaoDetalheResponse(
                800001L,
                "MARIA APARECIDA",
                "50000800001",
                "800000000800001",
                LocalDate.of(1971, 3, 15),
                53,
                "FEMININO",
                "(47) 9 12345678",
                null,
                900001L,
                new UnidadeSaudeResponse(1001L, "2748304", "UBS", "RUA A, 1 - CENTRO", "89119210"),
                new EquipeResponse(2001L, "ESF A", "0000068017")
        ));

        mockMvc.perform(get("/api/v1/cidadao/50000800001").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coSeqCidadao").value(800001L));
    }

    @Test
    void buscarCidadaoCpfNaoEncontrado_deveRetornar404() throws Exception {
        when(cidadaoService.buscarPorCpf("50000800999"))
                .thenThrow(new CidadaoNaoEncontradoException("Nao encontrado"));

        mockMvc.perform(get("/api/v1/cidadao/50000800999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarCidadaoCpfInvalido_deveRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/cidadao/123"))
                .andExpect(status().isBadRequest());
    }
}

