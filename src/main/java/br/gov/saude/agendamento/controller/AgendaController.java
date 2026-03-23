package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.request.AgendarRequest;
import br.gov.saude.agendamento.dto.request.CancelarRequest;
import br.gov.saude.agendamento.dto.response.AgendamentoCriadoResponse;
import br.gov.saude.agendamento.dto.response.AgendamentoResponse;
import br.gov.saude.agendamento.dto.response.CancelamentoResponse;
import br.gov.saude.agendamento.dto.response.GradeResponse;
import br.gov.saude.agendamento.service.AgendaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/agenda")
public class AgendaController {

    private static final Logger log = LoggerFactory.getLogger(AgendaController.class);

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @GetMapping("/{coLotacao}/grade")
    public GradeResponse obterGrade(
            @PathVariable Long coLotacao,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        log.info("FLOW_START controller=AgendaController metodo=obterGrade coLotacao={} data={}", coLotacao, data);

        GradeResponse response = agendaService.obterGrade(coLotacao, data);

        log.info("FLOW_END controller=AgendaController metodo=obterGrade coLotacao={} data={} totalSlots={} slotsDisponiveis={}",
                coLotacao,
                data,
                response.totalSlots(),
                response.slotsDisponiveis());

        return response;
    }

    @GetMapping("/{coLotacao}/agendamentos")
    public List<AgendamentoResponse> listarAgendamentos(
            @PathVariable Long coLotacao,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        log.info("FLOW_START controller=AgendaController metodo=listarAgendamentos coLotacao={} inicio={} fim={}",
                coLotacao,
                inicio,
                fim);

        List<AgendamentoResponse> response = agendaService.listarAgendamentos(coLotacao, inicio, fim);

        log.info("FLOW_END controller=AgendaController metodo=listarAgendamentos coLotacao={} totalRegistros={}",
                coLotacao,
                response.size());

        return response;
    }

    @PostMapping("/agendar")
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoCriadoResponse agendar(@Valid @RequestBody AgendarRequest request) {
        log.info("FLOW_START controller=AgendaController metodo=agendar coLotacao={} coProntuario={} data={} horaInicio={}",
                request.coLotacao(),
                request.coProntuario(),
                request.data(),
                request.horaInicio());

        AgendamentoCriadoResponse response = agendaService.agendar(request);

        log.info("FLOW_END controller=AgendaController metodo=agendar coAgendado={} uuidAgendamento={} status={}",
                response.coAgendado(),
                response.uuidAgendamento(),
                response.status());

        return response;
    }

    @PatchMapping("/{coAgendado}/cancelar")
    public CancelamentoResponse cancelar(@PathVariable Long coAgendado, @Valid @RequestBody CancelarRequest request) {
        log.info("FLOW_START controller=AgendaController metodo=cancelar coAgendado={} motivoCancelamento={}",
                coAgendado,
                request.motivoCancelamento());

        CancelamentoResponse response = agendaService.cancelar(coAgendado, request);

        log.info("FLOW_END controller=AgendaController metodo=cancelar coAgendado={} status={}",
                response.coAgendado(),
                response.status());

        return response;
    }
}

