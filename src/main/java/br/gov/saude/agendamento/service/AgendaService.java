package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.domain.entity.Agendado;
import br.gov.saude.agendamento.dto.request.AgendarRequest;
import br.gov.saude.agendamento.dto.request.CancelarRequest;
import br.gov.saude.agendamento.dto.response.AgendamentoCriadoResponse;
import br.gov.saude.agendamento.dto.response.AgendamentoResponse;
import br.gov.saude.agendamento.dto.response.CancelamentoResponse;
import br.gov.saude.agendamento.dto.response.GradeResponse;
import br.gov.saude.agendamento.dto.response.SlotResponse;
import br.gov.saude.agendamento.exception.AgendaNaoConfiguradaException;
import br.gov.saude.agendamento.exception.AgendamentoDuplicadoException;
import br.gov.saude.agendamento.exception.CancelamentoInvalidoException;
import br.gov.saude.agendamento.exception.LotacaoNaoEncontradaException;
import br.gov.saude.agendamento.exception.PeriodoMaximoExcedidoException;
import br.gov.saude.agendamento.exception.RecursoNaoEncontradoException;
import br.gov.saude.agendamento.exception.SlotIndisponivelException;
import br.gov.saude.agendamento.repository.AgendadoRepository;
import br.gov.saude.agendamento.repository.CfgAgendaRepository;
import br.gov.saude.agendamento.repository.LotacaoRepository;
import br.gov.saude.agendamento.repository.ProntuarioRepository;
import br.gov.saude.agendamento.repository.projection.GradeSlotProjection;
import br.gov.saude.agendamento.repository.projection.LotacaoDetalheProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AgendaService {

    private static final long STATUS_AGENDADO = 1L;

    private final LotacaoRepository lotacaoRepository;
    private final ProntuarioRepository prontuarioRepository;
    private final CfgAgendaRepository cfgAgendaRepository;
    private final AgendadoRepository agendadoRepository;

    public AgendaService(LotacaoRepository lotacaoRepository,
                         ProntuarioRepository prontuarioRepository,
                         CfgAgendaRepository cfgAgendaRepository,
                         AgendadoRepository agendadoRepository) {
        this.lotacaoRepository = lotacaoRepository;
        this.prontuarioRepository = prontuarioRepository;
        this.cfgAgendaRepository = cfgAgendaRepository;
        this.agendadoRepository = agendadoRepository;
    }

    @Transactional(readOnly = true)
    public GradeResponse obterGrade(Long coLotacao, LocalDate data) {
        LotacaoDetalheProjection detalhe = lotacaoRepository.buscarDetalheLotacao(coLotacao)
                .orElseThrow(() -> new LotacaoNaoEncontradaException("Lotacao informada nao foi encontrada."));

        if (isFimDeSemana(data)) {
            return gradeVaziaComMensagem(
                    coLotacao,
                    detalhe.getNomeProfissional(),
                    detalhe.getNoCbo(),
                    data,
                    "Sem agenda configurada para este dia da semana."
            );
        }

        if (!cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)) {
            throw new AgendaNaoConfiguradaException("Profissional sem agenda configurada.");
        }

        int diaSemana = data.getDayOfWeek().getValue();
        List<GradeSlotProjection> slotsRaw = agendadoRepository.buscarGrade(coLotacao, data.toString(), diaSemana);

        List<SlotResponse> slots = slotsRaw.stream()
                .map(slot -> new SlotResponse(
                        slot.getHorarioInicio(),
                        slot.getHorarioFim(),
                        slot.getPeriodo(),
                        slot.getSituacao(),
                        slot.getCoSeqAgendado(),
                        slot.getStatusAgendamento(),
                        slot.getPaciente()
                ))
                .toList();

        String motivoBloqueio = slotsRaw.stream()
                .map(GradeSlotProjection::getMotivoBloqueio)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        int total = slots.size();
        int disponiveis = (int) slots.stream().filter(s -> "DISPONIVEL".equals(s.situacao())).count();
        int ocupados = (int) slots.stream().filter(s -> "OCUPADO".equals(s.situacao())).count();
        int bloqueados = (int) slots.stream().filter(s -> "BLOQUEADO".equals(s.situacao())).count();

        return new GradeResponse(
                coLotacao,
                detalhe.getNomeProfissional(),
                detalhe.getNoCbo(),
                data,
                data.getDayOfWeek().name(),
                bloqueados > 0,
                motivoBloqueio,
                null,
                slots,
                total,
                disponiveis,
                ocupados,
                bloqueados
        );
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarAgendamentos(Long coLotacao, LocalDate inicio, LocalDate fim) {
        long dias = ChronoUnit.DAYS.between(inicio, fim);
        if (dias < 0) {
            throw new IllegalArgumentException("Data inicial deve ser menor ou igual a data final.");
        }
        if (dias > 60) {
            throw new PeriodoMaximoExcedidoException("O periodo informado nao pode ultrapassar 60 dias.");
        }

        return agendadoRepository.listarAgendamentosPeriodo(coLotacao, inicio, fim)
                .stream()
                .map(item -> new AgendamentoResponse(
                        item.getCoSeqAgendado(),
                        item.getData(),
                        item.getHoraInicio(),
                        item.getStatusId(),
                        item.getMotivo(),
                        item.getPaciente(),
                        item.getNuCpf(),
                        item.getNuTelefoneCelular(),
                        item.getDsObservacao()
                ))
                .toList();
    }

    @Transactional
    public AgendamentoCriadoResponse agendar(AgendarRequest request) {
        // 1. Lotacao ativa
        lotacaoRepository.buscarLotacaoAtiva(request.coLotacao())
                .orElseThrow(() -> new LotacaoNaoEncontradaException("Lotacao inexistente ou inativa."));

        // 2. Prontuario existe
        prontuarioRepository.findById(request.coProntuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prontuario nao encontrado."));

        // 3. Agenda configurada
        if (!cfgAgendaRepository.existsByCoEntidadeConfigurada(request.coLotacao())) {
            throw new AgendaNaoConfiguradaException("Profissional sem agenda configurada.");
        }

        // 4. Nao pode ser fim de semana
        if (isFimDeSemana(request.data())) {
            throw new IllegalArgumentException("Nao e permitido agendar em final de semana.");
        }

        // 5. Data nao pode ser passada
        if (request.data().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Nao e permitido agendar em data passada.");
        }

        // 6. Slot disponivel (reuso da mesma logica da grade)
        GradeResponse grade = obterGrade(request.coLotacao(), request.data());
        String hora = request.horaInicio().toString();
        SlotResponse slot = grade.slots()
                .stream()
                .filter(s -> hora.equals(s.horaInicio()))
                .findFirst()
                .orElseThrow(() -> new SlotIndisponivelException("Slot nao encontrado para a hora informada."));

        if (!"DISPONIVEL".equals(slot.situacao())) {
            throw new SlotIndisponivelException("Slot selecionado nao esta disponivel.");
        }

        // 7. Nao pode ter agendamento ativo no mesmo dia/profissional
        long quantidade = agendadoRepository.contarAgendamentoAtivoMesmoDia(
                request.coLotacao(),
                request.coProntuario(),
                request.data()
        );
        if (quantidade > 0) {
            throw new AgendamentoDuplicadoException("Cidadao ja possui agendamento ativo no mesmo dia para este profissional.");
        }

        Long motivoReserva = request.coMotivoReserva() == null ? 1L : request.coMotivoReserva();
        String uuid = UUID.randomUUID().toString();

        Long coAgendado = agendadoRepository.inserirAgendamento(
                request.coLotacao(),
                request.coProntuario(),
                request.data().toString(),
                hora,
                request.coOrigem(),
                motivoReserva,
                request.observacao(),
                uuid
        );

        return new AgendamentoCriadoResponse(
                coAgendado,
                request.coLotacao(),
                request.coProntuario(),
                request.data(),
                hora,
                "AGENDADO",
                uuid
        );
    }

    @Transactional
    public CancelamentoResponse cancelar(Long coAgendado, CancelarRequest request) {
        Agendado agendamento = agendadoRepository.findById(coAgendado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento nao encontrado."));

        if (agendamento.getStAgendado() == null || agendamento.getStAgendado() != STATUS_AGENDADO) {
            throw new CancelamentoInvalidoException("Somente agendamentos com status AGENDADO podem ser cancelados.");
        }

        Long novoStatus = switch (request.motivoCancelamento()) {
            case "CIDADAO" -> 2L;
            case "PROFISSIONAL" -> 3L;
            default -> throw new IllegalArgumentException("Motivo de cancelamento invalido.");
        };

        agendamento.setStAgendado(novoStatus);
        agendadoRepository.save(agendamento);

        String status = novoStatus == 2L ? "CANCELADO_CIDADAO" : "CANCELADO_PROFISSIONAL";
        return new CancelamentoResponse(coAgendado, status, "Agendamento cancelado com sucesso.");
    }

    private boolean isFimDeSemana(LocalDate data) {
        DayOfWeek dayOfWeek = data.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private GradeResponse gradeVaziaComMensagem(Long coLotacao, String profissional, String cbo, LocalDate data, String mensagem) {
        return new GradeResponse(
                coLotacao,
                profissional,
                cbo,
                data,
                data.getDayOfWeek().name(),
                false,
                null,
                mensagem,
                Collections.emptyList(),
                0,
                0,
                0,
                0
        );
    }
}
