package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.response.CidadaoDetalheResponse;
import br.gov.saude.agendamento.service.CidadaoService;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/cidadao")
public class CidadaoController {

    private static final Logger log = LoggerFactory.getLogger(CidadaoController.class);

    private final CidadaoService cidadaoService;

    public CidadaoController(CidadaoService cidadaoService) {
        this.cidadaoService = cidadaoService;
    }

    @GetMapping("/{cpf}")
    public CidadaoDetalheResponse buscarPorCpf(
            @PathVariable
            @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 digitos numericos")
            String cpf
    ) {
        log.info("FLOW_START controller=CidadaoController metodo=buscarPorCpf cpf={}", cpf);

        CidadaoDetalheResponse response = cidadaoService.buscarPorCpf(cpf);

        log.info("FLOW_END controller=CidadaoController metodo=buscarPorCpf cpf={} coSeqCidadao={} coSeqEquipe={}",
                cpf,
                response.coSeqCidadao(),
                response.equipe() == null ? null : response.equipe().coSeq());

        return response;
    }
}

