package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.response.ProfissionalResponse;
import br.gov.saude.agendamento.service.EquipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipe")
public class EquipeController {

    private static final Logger log = LoggerFactory.getLogger(EquipeController.class);

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @GetMapping("/{coSeqEquipe}/profissionais")
    public List<ProfissionalResponse> listarProfissionais(@PathVariable Long coSeqEquipe) {
        log.info("FLOW_START controller=EquipeController metodo=listarProfissionais coSeqEquipe={}", coSeqEquipe);

        List<ProfissionalResponse> response = equipeService.listarProfissionais(coSeqEquipe);

        log.info("FLOW_END controller=EquipeController metodo=listarProfissionais coSeqEquipe={} totalRegistros={}",
                coSeqEquipe,
                response.size());

        return response;
    }
}

