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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final long STATUS_AGENDADO = 0L;
    private static final Logger log = LoggerFactory.getLogger(AgendaService.class);

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
        log.info("FLOW_START service=AgendaService metodo=obterGrade coLotacao={} data={}", coLotacao, data);

        LotacaoDetalheProjection detalhe = lotacaoRepository.buscarDetalheLotacao(coLotacao)
                .orElseThrow(() -> new LotacaoNaoEncontradaException("Lotacao informada nao foi encontrada."));

        log.info("FLOW_CRITICAL service=AgendaService metodo=obterGrade etapa=lotacaoEncontrada coLotacao={} profissional={} cbo={}",
                coLotacao,
                detalhe.getNomeProfissional(),
                detalhe.getNoCbo());

        if (isFimDeSemana(data)) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=obterGrade etapa=fimDeSemana coLotacao={} data={}", coLotacao, data);
            GradeResponse response = gradeVaziaComMensagem(
                    coLotacao,
                    detalhe.getNomeProfissional(),
                    detalhe.getNoCbo(),
                    data,
                    "Sem agenda configurada para este dia da semana."
            );

            log.info("FLOW_END service=AgendaService metodo=obterGrade coLotacao={} data={} totalSlots={} mensagem={}",
                    coLotacao,
                    data,
                    response.totalSlots(),
                    response.mensagem());
            return response;
        }

        if (!cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=obterGrade etapa=agendaNaoConfigurada coLotacao={} data={}", coLotacao, data);
            throw new AgendaNaoConfiguradaException("Profissional sem agenda configurada.");
        }

        int diaSemana = data.getDayOfWeek().getValue();
        List<GradeSlotProjection> slotsRaw = agendadoRepository.buscarGrade(coLotacao, data.toString(), diaSemana);

        log.info("FLOW_CRITICAL service=AgendaService metodo=obterGrade etapa=gradeConsultada coLotacao={} data={} diaSemana={} totalSlotsRaw={}",
                coLotacao,
                data,
                diaSemana,
                slotsRaw.size());

        List<SlotResponse> slots = slotsRaw.stream()
                .map(slot -> new SlotResponse(
                        slot.getHorarioInicio(),
                        slot.getHorarioFim(),
                        slot.getPeriodo(),
                        slot.getSituacao(),
                        slot.getCoSeqAgendado(),
                        slot.getProntuario(),
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

        GradeResponse response = new GradeResponse(
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

        log.info("FLOW_END service=AgendaService metodo=obterGrade coLotacao={} data={} totalSlots={} disponiveis={} ocupados={} bloqueados={}",
                coLotacao,
                data,
                response.totalSlots(),
                response.slotsDisponiveis(),
                response.slotsOcupados(),
                response.slotsBloqueados());

        return response;
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarAgendamentos(Long coLotacao, LocalDate inicio, LocalDate fim) {
        log.info("FLOW_START service=AgendaService metodo=listarAgendamentos coLotacao={} inicio={} fim={}",
                coLotacao,
                inicio,
                fim);

        long dias = ChronoUnit.DAYS.between(inicio, fim);
        if (dias < 0) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=listarAgendamentos etapa=periodoInvalido coLotacao={} inicio={} fim={}",
                    coLotacao,
                    inicio,
                    fim);
            throw new IllegalArgumentException("Data inicial deve ser menor ou igual a data final.");
        }
        if (dias > 60) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=listarAgendamentos etapa=periodoMaximoExcedido coLotacao={} dias={}",
                    coLotacao,
                    dias);
            throw new PeriodoMaximoExcedidoException("O periodo informado nao pode ultrapassar 60 dias.");
        }

        List<AgendamentoResponse> response = agendadoRepository.listarAgendamentosPeriodo(coLotacao, inicio, fim)
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

        log.info("FLOW_END service=AgendaService metodo=listarAgendamentos coLotacao={} totalRegistros={}",
                coLotacao,
                response.size());

        return response;
    }

    @Transactional
    public AgendamentoCriadoResponse agendar(AgendarRequest request) {
        log.info("FLOW_START service=AgendaService metodo=agendar coLotacao={} coProntuario={} data={} horaInicio={}",
                request.coLotacao(),
                request.coProntuario(),
                request.data(),
                request.horaInicio());

        // 1. Lotacao ativa
        lotacaoRepository.buscarLotacaoAtiva(request.coLotacao())
                .orElseThrow(() -> new LotacaoNaoEncontradaException("Lotacao inexistente ou inativa."));

        log.info("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=lotacaoAtivaValidada coLotacao={}", request.coLotacao());

        // 2. Prontuario existe
        prontuarioRepository.findById(request.coProntuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prontuario nao encontrado."));

        log.info("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=prontuarioValidado coProntuario={}", request.coProntuario());

        // 3. Agenda configurada
        if (!cfgAgendaRepository.existsByCoEntidadeConfigurada(request.coLotacao())) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=agendaNaoConfigurada coLotacao={}", request.coLotacao());
            throw new AgendaNaoConfiguradaException("Profissional sem agenda configurada.");
        }

        log.info("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=agendaConfigurada coLotacao={}", request.coLotacao());

        // 4. Nao pode ser fim de semana
        if (isFimDeSemana(request.data())) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=fimDeSemana data={}", request.data());
            throw new IllegalArgumentException("Nao e permitido agendar em final de semana.");
        }

        // 5. Data nao pode ser passada
        if (request.data().isBefore(LocalDate.now())) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=dataPassada data={}", request.data());
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

        log.info("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=slotLocalizado horaInicio={} situacao={}",
                hora,
                slot.situacao());

        if (!"DISPONIVEL".equals(slot.situacao())) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=slotIndisponivel horaInicio={} situacao={}",
                    hora,
                    slot.situacao());
            throw new SlotIndisponivelException("Slot selecionado nao esta disponivel.");
        }

        // 7. Nao pode ter agendamento ativo no mesmo dia/profissional
        long quantidade = agendadoRepository.contarAgendamentoAtivoMesmoDia(
                request.coLotacao(),
                request.coProntuario(),
                request.data()
        );
        log.info("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=validacaoDuplicidade quantidadeAgendamentosAtivos={}", quantidade);
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

        log.info("FLOW_CRITICAL service=AgendaService metodo=agendar etapa=agendamentoInserido coAgendado={} protocolo={}",
                coAgendado,
                uuid);

        AgendamentoCriadoResponse response = new AgendamentoCriadoResponse(
                coAgendado,
                request.coLotacao(),
                request.coProntuario(),
                request.data(),
                hora,
                "AGENDADO",
                uuid
        );

        log.info("FLOW_END service=AgendaService metodo=agendar coAgendado={} status={} uuidAgendamento={}",
                response.coAgendado(),
                response.status(),
                response.uuidAgendamento());

        return response;
    }

    @Transactional
    public CancelamentoResponse cancelar(Long coAgendado, CancelarRequest request) {
        log.info("FLOW_START service=AgendaService metodo=cancelar coAgendado={} motivoCancelamento={}",
                coAgendado,
                request.motivoCancelamento());

        Agendado agendamento = agendadoRepository.findById(coAgendado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento nao encontrado."));

        log.info("FLOW_CRITICAL service=AgendaService metodo=cancelar etapa=agendamentoEncontrado coAgendado={} statusAtual={}",
                coAgendado,
                agendamento.getStAgendado());

        if (agendamento.getStAgendado() == null || agendamento.getStAgendado() != STATUS_AGENDADO) {
            log.warn("FLOW_CRITICAL service=AgendaService metodo=cancelar etapa=statusInvalido coAgendado={} statusAtual={}",
                    coAgendado,
                    agendamento.getStAgendado());
            throw new CancelamentoInvalidoException("Somente agendamentos com status AGENDADO podem ser cancelados.");
        }

        Long novoStatus = switch (request.motivoCancelamento()) {
            case "CIDADAO", "PROFISSIONAL" -> 4L;
            default -> throw new IllegalArgumentException("Motivo de cancelamento invalido.");
        };

        agendamento.setStAgendado(novoStatus);
        agendadoRepository.save(agendamento);

        log.info("FLOW_CRITICAL service=AgendaService metodo=cancelar etapa=statusAtualizado coAgendado={} novoStatus={}",
                coAgendado,
                novoStatus);

        String status = "CANCELADO";
        CancelamentoResponse response = new CancelamentoResponse(coAgendado, status, "Agendamento cancelado com sucesso.");

        log.info("FLOW_END service=AgendaService metodo=cancelar coAgendado={} status={}",
                response.coAgendado(),
                response.status());

        return response;
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
