package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.dto.response.DominioResponse;
import br.gov.saude.agendamento.repository.MotivoReservaRepository;
import br.gov.saude.agendamento.repository.OrigemRepository;
import br.gov.saude.agendamento.repository.SituacaoAgendadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DominioService {

    private static final Logger log = LoggerFactory.getLogger(DominioService.class);

    private final MotivoReservaRepository motivoReservaRepository;
    private final OrigemRepository origemRepository;
    private final SituacaoAgendadoRepository situacaoAgendadoRepository;

    public DominioService(MotivoReservaRepository motivoReservaRepository,
                          OrigemRepository origemRepository,
                          SituacaoAgendadoRepository situacaoAgendadoRepository) {
        this.motivoReservaRepository = motivoReservaRepository;
        this.origemRepository = origemRepository;
        this.situacaoAgendadoRepository = situacaoAgendadoRepository;
    }

    public List<DominioResponse> listarMotivosReserva() {
        log.info("FLOW_START service=DominioService metodo=listarMotivosReserva");

        List<DominioResponse> response = motivoReservaRepository.findAllByOrderByCoAsc().stream()
                .map(item -> new DominioResponse(item.getCo(), item.getDescricao()))
                .toList();

        log.info("FLOW_END service=DominioService metodo=listarMotivosReserva totalRegistros={}", response.size());
        return response;
    }

    public List<DominioResponse> listarOrigens() {
        log.info("FLOW_START service=DominioService metodo=listarOrigens");

        List<DominioResponse> response = origemRepository.findAllByOrderByCoAsc().stream()
                .map(item -> new DominioResponse(item.getCo(), item.getDescricao()))
                .toList();

        log.info("FLOW_END service=DominioService metodo=listarOrigens totalRegistros={}", response.size());
        return response;
    }

    public List<DominioResponse> listarSituacoesAgendado() {
        log.info("FLOW_START service=DominioService metodo=listarSituacoesAgendado");

        List<DominioResponse> response = situacaoAgendadoRepository.findAllByOrderByCoSituacaoAgendadoAsc().stream()
                .map(item -> new DominioResponse(item.getCoSituacaoAgendado(), item.getDescricao()))
                .toList();

        log.info("FLOW_END service=DominioService metodo=listarSituacoesAgendado totalRegistros={}", response.size());
        return response;
    }
}

