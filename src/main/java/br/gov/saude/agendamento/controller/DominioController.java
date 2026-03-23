package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.response.DominioResponse;
import br.gov.saude.agendamento.service.DominioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dominios")
public class DominioController {

    private static final Logger log = LoggerFactory.getLogger(DominioController.class);

    private final DominioService dominioService;

    public DominioController(DominioService dominioService) {
        this.dominioService = dominioService;
    }

    @GetMapping("/motivos-reserva")
    public List<DominioResponse> motivosReserva() {
        log.info("FLOW_START controller=DominioController metodo=motivosReserva");
        List<DominioResponse> response = dominioService.listarMotivosReserva();
        log.info("FLOW_END controller=DominioController metodo=motivosReserva totalRegistros={}", response.size());
        return response;
    }

    @GetMapping("/origens")
    public List<DominioResponse> origens() {
        log.info("FLOW_START controller=DominioController metodo=origens");
        List<DominioResponse> response = dominioService.listarOrigens();
        log.info("FLOW_END controller=DominioController metodo=origens totalRegistros={}", response.size());
        return response;
    }

    @GetMapping("/situacoes-agendado")
    public List<DominioResponse> situacoesAgendado() {
        log.info("FLOW_START controller=DominioController metodo=situacoesAgendado");
        List<DominioResponse> response = dominioService.listarSituacoesAgendado();
        log.info("FLOW_END controller=DominioController metodo=situacoesAgendado totalRegistros={}", response.size());
        return response;
    }
}

