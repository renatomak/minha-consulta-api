package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.response.DominioResponse;
import br.gov.saude.agendamento.service.DominioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dominios")
public class DominioController {

    private final DominioService dominioService;

    public DominioController(DominioService dominioService) {
        this.dominioService = dominioService;
    }

    @GetMapping("/motivos-reserva")
    public List<DominioResponse> motivosReserva() {
        return dominioService.listarMotivosReserva();
    }

    @GetMapping("/origens")
    public List<DominioResponse> origens() {
        return dominioService.listarOrigens();
    }

    @GetMapping("/situacoes-agendado")
    public List<DominioResponse> situacoesAgendado() {
        return dominioService.listarSituacoesAgendado();
    }
}

