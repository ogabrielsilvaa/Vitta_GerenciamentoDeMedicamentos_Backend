package com.vitta.vittaBackend.service;

import com.vitta.vittaBackend.dto.request.agendamento.AgendamentoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.request.agendamento.AgendamentoDTORequest;
import com.vitta.vittaBackend.dto.request.medicamentoHistorico.RegistrarUsoDTORequest;
import com.vitta.vittaBackend.dto.response.agendamento.AgendamentoDTOResponse;
import com.vitta.vittaBackend.dto.response.medicamentoHistorico.MedicamentoHistoricoDTOResponse;
import com.vitta.vittaBackend.entity.*;
import com.vitta.vittaBackend.enums.TipoFrequencia;
import com.vitta.vittaBackend.enums.agendamento.AgendamentoStatus;
import com.vitta.vittaBackend.enums.agendamento.TipoDeAlerta;
import com.vitta.vittaBackend.enums.tratamento.TratamentoStatus;
import com.vitta.vittaBackend.repository.AgendamentoRepository;
import com.vitta.vittaBackend.repository.TratamentoRepository;
import com.vitta.vittaBackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço {@link com.vitta.vittaBackend.service.AgendamentoService}.
 * <p>
 * Testa as operações de CRUD, a lógica de conclusão de agendamentos e a
 * geração automática de horários baseada nas regras do tratamento.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TratamentoRepository tratamentoRepository;

    @Mock
    private TratamentoService tratamentoService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario usuarioMock;
    private Tratamento tratamentoMock;
    private Agendamento agendamentoMock;
    private Medicamento medicamentoMock;

    /**
     * Configuração inicial dos cenários de teste.
     * Cria instâncias válidas de Usuario, Medicamento, Tratamento e Agendamento.
     */
    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNome("Usuario Teste");

        medicamentoMock = new Medicamento();
        medicamentoMock.setId(10);
        medicamentoMock.setNome("Dipirona");

        tratamentoMock = new Tratamento();
        tratamentoMock.setId(100);
        tratamentoMock.setUsuario(usuarioMock);
        tratamentoMock.setMedicamento(medicamentoMock);
        tratamentoMock.setStatus(TratamentoStatus.ATIVO);
        tratamentoMock.setDataDeInicio(LocalDate.now());
        tratamentoMock.setDataDeTermino(LocalDate.now().plusDays(5));

        agendamentoMock = new Agendamento();
        agendamentoMock.setId(500);
        agendamentoMock.setHorarioDoAgendamento(LocalDateTime.now().plusHours(2));
        agendamentoMock.setStatus(AgendamentoStatus.PENDENTE);
        agendamentoMock.setTipoDeAlerta(TipoDeAlerta.NOTIFICACAO_PUSH);
        agendamentoMock.setUsuario(usuarioMock);
        agendamentoMock.setTratamento(tratamentoMock);
    }

    /**
     * Testa a criação de um agendamento individual com sucesso.
     * Verifica se as validações de data do tratamento são respeitadas.
     */
    @Test
    @DisplayName("Deve criar agendamento com sucesso dentro do prazo do tratamento")
    void deveCriarAgendamento() {
        AgendamentoDTORequest request = new AgendamentoDTORequest();
        request.setTratamentoId(100);
        request.setHorarioDoAgendamento(LocalDateTime.now().plusDays(1).withHour(10));
        request.setTipoDeAlerta(1);

        when(usuarioRepository.getReferenceById(1)).thenReturn(usuarioMock);
        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoMock);

        AgendamentoDTOResponse response = agendamentoService.criarAgendamento(request, 1);

        assertNotNull(response);
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }

    /**
     * Testa a falha ao tentar criar agendamento fora da data de validade do tratamento.
     */
    @Test
    @DisplayName("Deve lançar erro ao criar agendamento após o término do tratamento")
    void deveFalharAgendamentoForaDaData() {
        AgendamentoDTORequest request = new AgendamentoDTORequest();
        request.setTratamentoId(100);
        request.setHorarioDoAgendamento(LocalDateTime.now().plusDays(20));

        when(usuarioRepository.getReferenceById(1)).thenReturn(usuarioMock);
        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);

        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoService.criarAgendamento(request, 1);
        });
    }

    /**
     * Testa a listagem de agendamentos de um usuário.
     */
    @Test
    @DisplayName("Deve listar agendamentos do usuário")
    void deveListarAgendamentosDoUsuario() {
        when(agendamentoRepository.listarAgendamentosAtivos(eq(1), any(), any()))
                .thenReturn(List.of(agendamentoMock));

        List<AgendamentoDTOResponse> lista = agendamentoService.listarAgendamentosDoUsuario(1, null, null);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    /**
     * Testa a conclusão bem-sucedida de um agendamento (Status PENDENTE -> TOMADO).
     * Verifica se o histórico é criado e se o serviço de tratamento é notificado.
     */
    @Test
    @DisplayName("Deve concluir agendamento (tomar medicamento)")
    void deveConcluirAgendamento() {
        RegistrarUsoDTORequest registro = new RegistrarUsoDTORequest();

        registro.setDoseTomada(BigDecimal.valueOf(1.0));

        registro.setHoraDoUso(LocalDateTime.now());
        registro.setObservacao("OK");

        when(usuarioRepository.getReferenceById(1)).thenReturn(usuarioMock);
        when(agendamentoRepository.findById(500)).thenReturn(Optional.of(agendamentoMock));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoMock);

        MedicamentoHistoricoDTOResponse response = agendamentoService.concluirAgendamento(500, registro, 1);

        assertNotNull(response);
        assertEquals(AgendamentoStatus.TOMADO, agendamentoMock.getStatus());
        assertNotNull(agendamentoMock.getMedicamentoHistorico());

        verify(tratamentoService, times(1)).verificarEConcluirTratamento(100, 1);
    }

    /**
     * Testa erro ao tentar concluir um agendamento que já foi tomado.
     */
    @Test
    @DisplayName("Deve falhar ao concluir agendamento que não está pendente")
    void deveFalharAoConcluirAgendamentoJaTomado() {
        agendamentoMock.setStatus(AgendamentoStatus.TOMADO);
        RegistrarUsoDTORequest registro = new RegistrarUsoDTORequest();

        when(usuarioRepository.getReferenceById(1)).thenReturn(usuarioMock);
        when(agendamentoRepository.findById(500)).thenReturn(Optional.of(agendamentoMock));

        assertThrows(IllegalStateException.class, () -> {
            agendamentoService.concluirAgendamento(500, registro, 1);
        });
    }

    /**
     * Testa a lógica de geração de agendamentos baseada em INTERVALO DE HORAS.
     * Ex: A cada 8 horas, durante 1 dia.
     */
    @Test
    @DisplayName("Deve gerar agendamentos por intervalo de horas")
    void deveGerarAgendamentosPorIntervalo() {
        Tratamento t = new Tratamento();
        t.setId(200);
        t.setDataDeInicio(LocalDate.of(2025, 1, 1));
        t.setDataDeTermino(LocalDate.of(2025, 1, 1));
        t.setTipoDeFrequencia(TipoFrequencia.INTERVALO_HORAS);
        t.setIntervaloEmHoras(8);
        t.setUsuario(usuarioMock);

        List<Agendamento> gerados = agendamentoService.gerarAgendamentosParaTratamento(t, TipoDeAlerta.NOTIFICACAO_PUSH);

        assertEquals(2, gerados.size());
        assertEquals(LocalDateTime.of(2025, 1, 1, 8, 0), gerados.get(0).getHorarioDoAgendamento());
        assertEquals(LocalDateTime.of(2025, 1, 1, 16, 0), gerados.get(1).getHorarioDoAgendamento());
    }

    /**
     * Testa a lógica de geração de agendamentos baseada em HORÁRIOS ESPECÍFICOS.
     * Ex: Todo dia às 09:00 e 21:00.
     */
    @Test
    @DisplayName("Deve gerar agendamentos por horários específicos")
    void deveGerarAgendamentosPorHorarioEspecifico() {
        Tratamento t = new Tratamento();
        t.setId(300);
        t.setDataDeInicio(LocalDate.of(2025, 1, 1));
        t.setDataDeTermino(LocalDate.of(2025, 1, 2));
        t.setTipoDeFrequencia(TipoFrequencia.HORARIOS_ESPECIFICOS);
        t.setHorariosEspecificos("09:00, 21:00");
        t.setUsuario(usuarioMock);

        List<Agendamento> gerados = agendamentoService.gerarAgendamentosParaTratamento(t, TipoDeAlerta.NOTIFICACAO_PUSH);

        assertEquals(4, gerados.size());
        assertEquals(LocalTime.of(9, 0), gerados.get(0).getHorarioDoAgendamento().toLocalTime());
        assertEquals(LocalTime.of(21, 0), gerados.get(1).getHorarioDoAgendamento().toLocalTime());
    }

    /**
     * Testa a exclusão lógica de um agendamento.
     */
    @Test
    @DisplayName("Deve deletar logicamente um agendamento")
    void deveDeletarAgendamento() {
        doNothing().when(agendamentoRepository).apagarLogicoAgendamento(500, 1);

        agendamentoService.deletarAgendamento(500, 1);

        verify(agendamentoRepository, times(1)).apagarLogicoAgendamento(500, 1);
    }

    /**
     * Testa a atualização de horário de um agendamento.
     */
    @Test
    @DisplayName("Deve atualizar horário do agendamento")
    void deveAtualizarAgendamento() {
        AgendamentoAtualizarDTORequest req = new AgendamentoAtualizarDTORequest();
        LocalDateTime novoHorario = LocalDateTime.now().plusHours(5);
        req.setHorarioDoAgendamento(novoHorario);

        when(agendamentoRepository.listarAgendamentoPorId(500, 1)).thenReturn(agendamentoMock);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(i -> i.getArgument(0));

        AgendamentoDTOResponse resp = agendamentoService.atualizarAgendamento(500, 1, req);

        assertEquals(novoHorario, agendamentoMock.getHorarioDoAgendamento());
        verify(agendamentoRepository, times(1)).save(agendamentoMock);
    }
}