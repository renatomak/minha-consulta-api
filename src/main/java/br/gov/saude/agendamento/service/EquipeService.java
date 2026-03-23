package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.dto.response.ProfissionalResponse;
import br.gov.saude.agendamento.repository.LotacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipeService {

    private static final Logger log = LoggerFactory.getLogger(EquipeService.class);

    private final LotacaoRepository lotacaoRepository;

    public EquipeService(LotacaoRepository lotacaoRepository) {
        this.lotacaoRepository = lotacaoRepository;
    }

    public List<ProfissionalResponse> listarProfissionais(Long coSeqEquipe) {
        log.info("FLOW_START service=EquipeService metodo=listarProfissionais coSeqEquipe={}", coSeqEquipe);

        List<ProfissionalResponse> response = lotacaoRepository.listarProfissionaisPorEquipe(coSeqEquipe)
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

        log.info("FLOW_END service=EquipeService metodo=listarProfissionais coSeqEquipe={} totalRegistros={}",
                coSeqEquipe,
                response.size());

        return response;
    }
}

