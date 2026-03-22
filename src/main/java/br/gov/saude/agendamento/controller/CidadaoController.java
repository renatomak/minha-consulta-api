package br.gov.saude.agendamento.controller;

import br.gov.saude.agendamento.dto.response.CidadaoDetalheResponse;
import br.gov.saude.agendamento.service.CidadaoService;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/cidadao")
public class CidadaoController {

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
        return cidadaoService.buscarPorCpf(cpf);
    }
}

