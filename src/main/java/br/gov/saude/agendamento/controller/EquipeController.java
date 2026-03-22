package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.response.ProfissionalResponse;
import br.gov.saude.agendamento.service.EquipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipe")
public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @GetMapping("/{coSeqEquipe}/profissionais")
    public List<ProfissionalResponse> listarProfissionais(@PathVariable Long coSeqEquipe) {
        return equipeService.listarProfissionais(coSeqEquipe);
    }
}

