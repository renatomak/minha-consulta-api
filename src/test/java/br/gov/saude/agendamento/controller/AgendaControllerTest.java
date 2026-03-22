package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.request.AgendarRequest;
import br.gov.saude.agendamento.dto.request.CancelarRequest;
import br.gov.saude.agendamento.dto.response.AgendamentoCriadoResponse;
import br.gov.saude.agendamento.exception.GlobalExceptionHandler;
import br.gov.saude.agendamento.exception.SlotIndisponivelException;
import br.gov.saude.agendamento.service.AgendaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgendaController.class)
@Import(GlobalExceptionHandler.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgendaService agendaService;

    @Test
    void agendar_comBodyValido_deveRetornar201() throws Exception {
        when(agendaService.agendar(any(AgendarRequest.class))).thenReturn(new AgendamentoCriadoResponse(
                991000L,
                500001L,
                900001L,
                LocalDate.now().plusDays(1),
                "08:20",
                "AGENDADO",
                "uuid"
        ));

        AgendarRequest request = new AgendarRequest(500001L, 900001L, LocalDate.now().plusDays(1), LocalTime.of(8, 20), 1L, 1L, "ok");

        mockMvc.perform(post("/api/v1/agenda/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void agendar_comSlotOcupado_deveRetornar409() throws Exception {
        when(agendaService.agendar(any(AgendarRequest.class))).thenThrow(new SlotIndisponivelException("ocupado"));
        AgendarRequest request = new AgendarRequest(500001L, 900001L, LocalDate.now().plusDays(1), LocalTime.of(8, 20), 1L, 1L, "ok");

        mockMvc.perform(post("/api/v1/agenda/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void agendar_semCampoObrigatorio_deveRetornar400() throws Exception {
        String body = """
                {
                  "coLotacao": 500001,
                  "data": "%s",
                  "horaInicio": "08:20",
                  "coOrigem": 1
                }
                """.formatted(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/agenda/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agendar_comDataNoPassado_deveRetornar400() throws Exception {
        when(agendaService.agendar(any(AgendarRequest.class))).thenThrow(new IllegalArgumentException("data passada"));
        AgendarRequest request = new AgendarRequest(500001L, 900001L, LocalDate.now().minusDays(1), LocalTime.of(8, 20), 1L, 1L, "ok");

        mockMvc.perform(post("/api/v1/agenda/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelar_comStatusInvalido_deveRetornar409() throws Exception {
        when(agendaService.cancelar(any(Long.class), any(CancelarRequest.class)))
                .thenThrow(new br.gov.saude.agendamento.exception.CancelamentoInvalidoException("invalido"));

        CancelarRequest request = new CancelarRequest("CIDADAO", "obs");

        mockMvc.perform(patch("/api/v1/agenda/990001/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}

