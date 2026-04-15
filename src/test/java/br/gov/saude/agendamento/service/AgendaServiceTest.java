package br.gov.saude.agendamento.service;

import br.gov.saude.agendamento.domain.entity.Agendado;
import br.gov.saude.agendamento.domain.entity.Lotacao;
import br.gov.saude.agendamento.domain.entity.Prontuario;
import br.gov.saude.agendamento.dto.request.AgendarRequest;
import br.gov.saude.agendamento.dto.request.CancelarRequest;
import br.gov.saude.agendamento.dto.response.GradeResponse;
import br.gov.saude.agendamento.exception.AgendaNaoConfiguradaException;
import br.gov.saude.agendamento.exception.AgendamentoDuplicadoException;
import br.gov.saude.agendamento.exception.CancelamentoInvalidoException;
import br.gov.saude.agendamento.exception.SlotIndisponivelException;
import br.gov.saude.agendamento.repository.AgendadoRepository;
import br.gov.saude.agendamento.repository.CfgAgendaRepository;
import br.gov.saude.agendamento.repository.LotacaoRepository;
import br.gov.saude.agendamento.repository.ProntuarioRepository;
import br.gov.saude.agendamento.repository.projection.GradeSlotProjection;
import br.gov.saude.agendamento.repository.projection.LotacaoDetalheProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private LotacaoRepository lotacaoRepository;
    @Mock
    private ProntuarioRepository prontuarioRepository;
    @Mock
    private CfgAgendaRepository cfgAgendaRepository;
    @Mock
    private AgendadoRepository agendadoRepository;

    @InjectMocks
    private AgendaService service;

    private final Long coLotacao = 500001L;
    private LocalDate dataUtilFutura;
    private int diaSemanaDataUtilFutura;

    @BeforeEach
    void setup() {
        // Usa sempre uma data futura em dia util para evitar flakiness temporal nos testes de agendamento.
        dataUtilFutura = proximaDataUtil(LocalDate.now());
        diaSemanaDataUtilFutura = dataUtilFutura.getDayOfWeek().getValue();
    }

    @Test
    void testarGradeRetornaSlotDisponivel() {
        stubDetalheLotacao();
        when(cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)).thenReturn(true);
        when(agendadoRepository.buscarGrade(coLotacao, "2026-03-23", 1)).thenReturn(List.of(slot("08:00", "08:20", "DISPONIVEL", null)));

        GradeResponse grade = service.obterGrade(coLotacao, LocalDate.of(2026, 3, 23));

        assertEquals(1, grade.totalSlots());
        assertEquals(1, grade.slotsDisponiveis());
    }

    @Test
    void testarGradeRetornaSlotOcupado() {
        stubDetalheLotacao();
        when(cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)).thenReturn(true);
        when(agendadoRepository.buscarGrade(coLotacao, "2026-03-23", 1)).thenReturn(List.of(slot("08:00", "08:20", "OCUPADO", null)));

        GradeResponse grade = service.obterGrade(coLotacao, LocalDate.of(2026, 3, 23));

        assertEquals(1, grade.slotsOcupados());
    }

    @Test
    void testarGradeRetornaBloqueadoQuandoFechamento() {
        stubDetalheLotacao();
        when(cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)).thenReturn(true);
        when(agendadoRepository.buscarGrade(coLotacao, "2026-03-23", 1)).thenReturn(List.of(slot("08:00", "08:20", "BLOQUEADO", "FERIAS")));

        GradeResponse grade = service.obterGrade(coLotacao, LocalDate.of(2026, 3, 23));

        assertTrue(grade.bloqueado());
        assertEquals("FERIAS", grade.motivoBloqueio());
    }

    @Test
    void testarGradeRetornaVaziaParaFimDeSemana() {
        stubDetalheLotacao();
        GradeResponse grade = service.obterGrade(coLotacao, LocalDate.of(2026, 3, 22));

        assertEquals(0, grade.totalSlots());
        assertEquals("Sem agenda configurada para este dia da semana.", grade.mensagem());
    }

    @Test
    void testarAgendarComSlotDisponivel_deveSalvar() {
        stubDetalheLotacao();
        when(lotacaoRepository.buscarLotacaoAtiva(coLotacao)).thenReturn(Optional.of(new Lotacao()));
        when(prontuarioRepository.findById(900001L)).thenReturn(Optional.of(new Prontuario()));
        when(cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)).thenReturn(true);
        when(agendadoRepository.buscarGrade(coLotacao, dataUtilFutura.toString(), diaSemanaDataUtilFutura)).thenReturn(List.of(slot("08:20", "08:40", "DISPONIVEL", null)));
        when(agendadoRepository.contarAgendamentoAtivoMesmoDia(coLotacao, 900001L, dataUtilFutura)).thenReturn(0L);
        when(agendadoRepository.inserirAgendamento(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(991000L);

        var response = service.agendar(new AgendarRequest(coLotacao, 900001L, dataUtilFutura, LocalTime.of(8, 20), 1L, 1L, "ok"));

        assertEquals(991000L, response.coAgendado());
    }

    @Test
    void testarAgendarComSlotOcupado_deveRetornar409() {
        stubDetalheLotacao();
        when(lotacaoRepository.buscarLotacaoAtiva(coLotacao)).thenReturn(Optional.of(new Lotacao()));
        when(prontuarioRepository.findById(900001L)).thenReturn(Optional.of(new Prontuario()));
        when(cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)).thenReturn(true);
        when(agendadoRepository.buscarGrade(coLotacao, dataUtilFutura.toString(), diaSemanaDataUtilFutura)).thenReturn(List.of(slot("08:20", "08:40", "OCUPADO", null)));

        assertThrows(SlotIndisponivelException.class,
                () -> service.agendar(new AgendarRequest(coLotacao, 900001L, dataUtilFutura, LocalTime.of(8, 20), 1L, 1L, "ok")));
    }

    @Test
    void testarAgendarComAgendaDuplicada_deveRetornar409() {
        stubDetalheLotacao();
        when(lotacaoRepository.buscarLotacaoAtiva(coLotacao)).thenReturn(Optional.of(new Lotacao()));
        when(prontuarioRepository.findById(900001L)).thenReturn(Optional.of(new Prontuario()));
        when(cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)).thenReturn(true);
        when(agendadoRepository.buscarGrade(coLotacao, dataUtilFutura.toString(), diaSemanaDataUtilFutura)).thenReturn(List.of(slot("08:20", "08:40", "DISPONIVEL", null)));
        when(agendadoRepository.contarAgendamentoAtivoMesmoDia(coLotacao, 900001L, dataUtilFutura)).thenReturn(1L);

        assertThrows(AgendamentoDuplicadoException.class,
                () -> service.agendar(new AgendarRequest(coLotacao, 900001L, dataUtilFutura, LocalTime.of(8, 20), 1L, 1L, "ok")));
    }

    @Test
    void testarAgendarSemAgendaConfigurada_deveRetornar422() {
        when(lotacaoRepository.buscarLotacaoAtiva(coLotacao)).thenReturn(Optional.of(new Lotacao()));
        when(prontuarioRepository.findById(900001L)).thenReturn(Optional.of(new Prontuario()));
        when(cfgAgendaRepository.existsByCoEntidadeConfigurada(coLotacao)).thenReturn(false);

        assertThrows(AgendaNaoConfiguradaException.class,
                () -> service.agendar(new AgendarRequest(coLotacao, 900001L, dataUtilFutura, LocalTime.of(8, 20), 1L, 1L, "ok")));
    }

    @Test
    void testarCancelarComStatusAgendado_deveAtualizar() {
        Agendado agendado = new Agendado();
        agendado.setStAgendado(0L);
        when(agendadoRepository.findById(990001L)).thenReturn(Optional.of(agendado));

        service.cancelar(990001L, new CancelarRequest("CIDADAO", "obs"));

        verify(agendadoRepository).save(agendado);
    }

    @Test
    void testarCancelarComStatusInvalido_deveRetornar409() {
        Agendado agendado = new Agendado();
        agendado.setStAgendado(5L);
        when(agendadoRepository.findById(990001L)).thenReturn(Optional.of(agendado));

        assertThrows(CancelamentoInvalidoException.class,
                () -> service.cancelar(990001L, new CancelarRequest("CIDADAO", "obs")));
    }

    private GradeSlotProjection slot(String inicio, String fim, String situacao, String motivoBloqueio) {
        return new GradeSlotProjection() {
            public String getHorarioInicio() { return inicio; }
            public String getHorarioFim() { return fim; }
            public String getPeriodo() { return "MANHA"; }
            public String getSituacao() { return situacao; }
            public Long getCoSeqAgendado() { return null; }

            @Override
            public Long getProntuario() {
                return 0L;
            }

            public String getStatusAgendamento() { return null; }
            public String getPaciente() { return null; }
            public String getMotivoBloqueio() { return motivoBloqueio; }
        };
    }

    private void stubDetalheLotacao() {
        when(lotacaoRepository.buscarDetalheLotacao(coLotacao)).thenReturn(Optional.of(new LotacaoDetalheProjection() {
            public Long getCoLotacao() { return coLotacao; }
            public String getNomeProfissional() { return "CARLOS"; }
            public String getNoCbo() { return "MEDICO"; }
        }));
    }

    private LocalDate proximaDataUtil(LocalDate base) {
        LocalDate data = base.plusDays(1);
        while (data.getDayOfWeek().getValue() >= 6) {
            data = data.plusDays(1);
        }
        return data;
    }
}
