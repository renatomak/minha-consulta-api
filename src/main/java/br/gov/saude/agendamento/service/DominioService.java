package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.dto.response.DominioResponse;
import br.gov.saude.agendamento.repository.MotivoReservaRepository;
import br.gov.saude.agendamento.repository.OrigemRepository;
import br.gov.saude.agendamento.repository.SituacaoAgendadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DominioService {

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
        return motivoReservaRepository.findAllByOrderByCoAsc().stream()
                .map(item -> new DominioResponse(item.getCo(), item.getDescricao()))
                .toList();
    }

    public List<DominioResponse> listarOrigens() {
        return origemRepository.findAllByOrderByCoAsc().stream()
                .map(item -> new DominioResponse(item.getCo(), item.getDescricao()))
                .toList();
    }

    public List<DominioResponse> listarSituacoesAgendado() {
        return situacaoAgendadoRepository.findAllByOrderByCoSituacaoAgendadoAsc().stream()
                .map(item -> new DominioResponse(item.getCoSituacaoAgendado(), item.getDescricao()))
                .toList();
    }
}

