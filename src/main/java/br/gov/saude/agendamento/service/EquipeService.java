package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.dto.response.ProfissionalResponse;
import br.gov.saude.agendamento.repository.LotacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipeService {

    private final LotacaoRepository lotacaoRepository;

    public EquipeService(LotacaoRepository lotacaoRepository) {
        this.lotacaoRepository = lotacaoRepository;
    }

    public List<ProfissionalResponse> listarProfissionais(Long coSeqEquipe) {
        return lotacaoRepository.listarProfissionaisPorEquipe(coSeqEquipe)
                .stream()
                .map(p -> new ProfissionalResponse(
                        p.getCoLotacao(),
                        p.getCoSeqProf(),
                        p.getNome(),
                        p.getNoCbo(),
                        p.getCoCbo(),
                        Boolean.TRUE.equals(p.getHasAgenda())
                ))
                .toList();
    }
}

