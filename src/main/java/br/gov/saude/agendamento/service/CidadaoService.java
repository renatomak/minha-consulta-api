package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.dto.response.CidadaoDetalheResponse;
import br.gov.saude.agendamento.dto.response.EquipeResponse;
import br.gov.saude.agendamento.dto.response.UnidadeSaudeResponse;
import br.gov.saude.agendamento.exception.CidadaoNaoEncontradoException;
import br.gov.saude.agendamento.exception.VinculoNaoEncontradoException;
import br.gov.saude.agendamento.repository.CidadaoRepository;
import br.gov.saude.agendamento.repository.CidadaoVinculacaoEquipeRepository;
import br.gov.saude.agendamento.repository.projection.CidadaoCpfProjection;
import br.gov.saude.agendamento.repository.projection.VinculoEquipeProjection;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class CidadaoService {

    private final CidadaoRepository cidadaoRepository;
    private final CidadaoVinculacaoEquipeRepository vinculacaoEquipeRepository;

    public CidadaoService(CidadaoRepository cidadaoRepository,
                          CidadaoVinculacaoEquipeRepository vinculacaoEquipeRepository) {
        this.cidadaoRepository = cidadaoRepository;
        this.vinculacaoEquipeRepository = vinculacaoEquipeRepository;
    }

    public CidadaoDetalheResponse buscarPorCpf(String cpf) {
        CidadaoCpfProjection cidadao = cidadaoRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> new CidadaoNaoEncontradoException("Nao foi encontrado cidadao ativo para o CPF informado."));

        VinculoEquipeProjection vinculo = vinculacaoEquipeRepository.buscarVinculoMaisRecente(cidadao.getCoSeqCidadao())
                .orElseThrow(() -> new VinculoNaoEncontradoException("Cidadao encontrado, mas sem vinculacao com UBS/equipe."));

        String endereco = "%s, %s - %s".formatted(
                nullToBlank(vinculo.getDsLogradouro()),
                nullToBlank(vinculo.getNuNumero()),
                nullToBlank(vinculo.getNoBairro())
        );

        return new CidadaoDetalheResponse(
                cidadao.getCoSeqCidadao(),
                cidadao.getNome(),
                cidadao.getNuCpf(),
                cidadao.getNuCns(),
                cidadao.getDtNascimento(),
                calcularIdade(cidadao.getDtNascimento()),
                cidadao.getNoSexo(),
                cidadao.getNuTelefoneCelular(),
                cidadao.getDsEmail(),
                cidadao.getCoProntuario(),
                new UnidadeSaudeResponse(
                        vinculo.getCoSeqUnidadeSaude(),
                        vinculo.getNuCnes(),
                        vinculo.getNoUnidadeSaude(),
                        endereco,
                        vinculo.getDsCep()
                ),
                new EquipeResponse(
                        vinculo.getCoSeqEquipe(),
                        vinculo.getNoEquipe(),
                        vinculo.getIneEquipe()
                )
        );
    }

    private Integer calcularIdade(LocalDate nascimento) {
        if (nascimento == null) {
            return null;
        }
        return Period.between(nascimento, LocalDate.now()).getYears();
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}

