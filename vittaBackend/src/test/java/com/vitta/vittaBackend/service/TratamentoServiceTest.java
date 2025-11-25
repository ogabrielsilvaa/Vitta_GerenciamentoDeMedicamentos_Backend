package com.vitta.vittaBackend.service;

import com.vitta.vittaBackend.dto.request.tratamento.TratamentoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.request.tratamento.TratamentoDTORequest;
import com.vitta.vittaBackend.dto.response.tratamento.TratamentoDTOResponse;
import com.vitta.vittaBackend.entity.Agendamento;
import com.vitta.vittaBackend.entity.Medicamento;
import com.vitta.vittaBackend.entity.Tratamento;
import com.vitta.vittaBackend.entity.Usuario;
import com.vitta.vittaBackend.enums.TipoFrequencia;
import com.vitta.vittaBackend.enums.agendamento.AgendamentoStatus;
import com.vitta.vittaBackend.enums.agendamento.TipoDeAlerta;
import com.vitta.vittaBackend.enums.tratamento.TratamentoStatus;
import com.vitta.vittaBackend.repository.AgendamentoRepository;
import com.vitta.vittaBackend.repository.MedicamentoRepository;
import com.vitta.vittaBackend.repository.TratamentoRepository;
import com.vitta.vittaBackend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço {@link TratamentoService}.
 * <p>
 * Verifica a lógica de criação de tratamentos, integração com a geração de agendamentos,
 * atualizações complexas (reagendamento) e conclusão automática.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class TratamentoServiceTest {

    @Mock
    private TratamentoRepository tratamentoRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AgendamentoService agendamentoService;

    @InjectMocks
    private TratamentoService tratamentoService;

    private Usuario usuarioMock;
    private Medicamento medicamentoMock;
    private Tratamento tratamentoMock;
    private TratamentoDTORequest tratamentoDTORequest;

    /**
     * Configuração inicial executada antes de cada teste.
     * Prepara objetos mocks básicos e inicializa listas mutáveis para evitar erros
     * em operações de manipulação de coleções durante os testes.
     */
    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNome("Usuário Teste");

        medicamentoMock = new Medicamento();
        medicamentoMock.setId(10);
        medicamentoMock.setNome("Amoxicilina");
        medicamentoMock.setUsuario(usuarioMock);

        tratamentoMock = new Tratamento();
        tratamentoMock.setId(100);
        tratamentoMock.setNome("Tratamento Infecção");
        tratamentoMock.setDosagem(BigDecimal.valueOf(500));
        tratamentoMock.setTipoDeFrequencia(TipoFrequencia.INTERVALO_HORAS);
        tratamentoMock.setIntervaloEmHoras(8);
        tratamentoMock.setDataDeInicio(LocalDate.now());
        tratamentoMock.setDataDeTermino(LocalDate.now().plusDays(7));
        tratamentoMock.setStatus(TratamentoStatus.ATIVO);
        tratamentoMock.setUsuario(usuarioMock);
        tratamentoMock.setMedicamento(medicamentoMock);
        tratamentoMock.setAgendamentos(new ArrayList<>());

        tratamentoDTORequest = new TratamentoDTORequest();
        tratamentoDTORequest.setMedicamentoId(10);
        tratamentoDTORequest.setNome("Tratamento Infecção");
        tratamentoDTORequest.setDosagem(BigDecimal.valueOf(500));
        tratamentoDTORequest.setDataDeInicio(LocalDate.now());
        tratamentoDTORequest.setDataDeTermino(LocalDate.now().plusDays(7));
        tratamentoDTORequest.setTipoDeFrequencia(1);
        tratamentoDTORequest.setIntervaloEmHoras(8);
    }

    /**
     * Testa a listagem de todos os tratamentos ativos de um usuário.
     */
    @Test
    @DisplayName("Deve listar tratamentos do usuário")
    void deveListarTratamentos() {
        when(tratamentoRepository.listarTratamentos(1)).thenReturn(List.of(tratamentoMock));

        List<TratamentoDTOResponse> lista = tratamentoService.listarTratamentos(1);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        verify(tratamentoRepository, times(1)).listarTratamentos(1);
    }

    /**
     * Testa a busca de um tratamento por ID com sucesso.
     */
    @Test
    @DisplayName("Deve buscar tratamento por ID")
    void deveBuscarTratamentoPorId() {
        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);

        TratamentoDTOResponse response = tratamentoService.listarTratamentoPorId(100, 1);

        assertNotNull(response);
        assertEquals(100, response.getId());
    }

    /**
     * Testa falha ao buscar tratamento inexistente.
     */
    @Test
    @DisplayName("Deve lançar exceção se tratamento não encontrado")
    void deveFalharBuscaTratamentoInexistente() {
        when(tratamentoRepository.listarTratamentoPorId(99, 1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            tratamentoService.listarTratamentoPorId(99, 1);
        });
    }

    /**
     * Testa o cadastro de um tratamento.
     * Verifica se o medicamento é validado, se o usuário é buscado e se o
     * serviço de agendamento é chamado para gerar as datas iniciais.
     */
    @Test
    @DisplayName("Deve cadastrar tratamento com sucesso")
    void deveCadastrarTratamento() {
        when(medicamentoRepository.listarMedicamentoPorId(10, 1)).thenReturn(medicamentoMock);
        when(usuarioRepository.getReferenceById(1)).thenReturn(usuarioMock);

        when(agendamentoService.gerarAgendamentosParaTratamento(any(Tratamento.class), any(TipoDeAlerta.class)))
                .thenReturn(new ArrayList<>());

        when(tratamentoRepository.save(any(Tratamento.class))).thenReturn(tratamentoMock);

        TratamentoDTOResponse response = tratamentoService.cadastrarTratamento(tratamentoDTORequest, 1);

        assertNotNull(response);
        verify(medicamentoRepository, times(1)).listarMedicamentoPorId(10, 1);
        verify(agendamentoService, times(1)).gerarAgendamentosParaTratamento(any(), any());
        verify(tratamentoRepository, times(1)).save(any(Tratamento.class));
    }

    /**
     * Testa falha no cadastro se o medicamento não pertencer ao usuário.
     */
    @Test
    @DisplayName("Deve falhar cadastro se medicamento não existir")
    void deveFalharCadastroSemMedicamento() {
        when(medicamentoRepository.listarMedicamentoPorId(10, 1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            tratamentoService.cadastrarTratamento(tratamentoDTORequest, 1);
        });
    }

    /**
     * Testa atualização simples (apenas nome/dosagem), sem alterar datas ou frequência.
     * Verifica se a regeneração de agendamentos NÃO é chamada.
     */
    @Test
    @DisplayName("Deve atualizar dados simples sem regerar agendamentos")
    void deveAtualizarSemRegerar() {
        TratamentoAtualizarDTORequest req = new TratamentoAtualizarDTORequest();
        req.setNome("Nome Atualizado");

        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);
        when(tratamentoRepository.save(any(Tratamento.class))).thenAnswer(i -> i.getArgument(0));

        TratamentoDTOResponse response = tratamentoService.atualizarTratamento(100, 1, req);

        assertEquals("Nome Atualizado", response.getNome());
        // Garante que NÃO chamou o metodo de gerar novos agendamentos
        verify(agendamentoService, never()).gerarAgendamentosFuturos(any(), any());
    }

    /**
     * Testa atualização complexa (alteração de frequência).
     * Verifica se os agendamentos pendentes são removidos e se novos são gerados.
     */
    @Test
    @DisplayName("Deve atualizar frequência e regerar agendamentos")
    void deveAtualizarERegerarAgendamentos() {
        Agendamento pendente = new Agendamento();
        pendente.setStatus(AgendamentoStatus.PENDENTE);
        tratamentoMock.getAgendamentos().add(pendente);

        TratamentoAtualizarDTORequest req = new TratamentoAtualizarDTORequest();
        req.setTipoDeFrequencia(2);

        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);
        when(agendamentoService.gerarAgendamentosFuturos(any(), any())).thenReturn(List.of(new Agendamento()));
        when(tratamentoRepository.save(any(Tratamento.class))).thenAnswer(i -> i.getArgument(0));

        tratamentoService.atualizarTratamento(100, 1, req);

        assertEquals(1, tratamentoMock.getAgendamentos().size());
        verify(agendamentoService, times(1)).gerarAgendamentosFuturos(any(), any());
    }

    /**
     * Testa a exclusão lógica do tratamento.
     * Verifica se os agendamentos pendentes são removidos e status muda para CANCELADO.
     */
    @Test
    @DisplayName("Deve deletar logicamente tratamento e limpar pendências")
    void deveDeletarLogico() {
        Agendamento pendente = new Agendamento();
        pendente.setStatus(AgendamentoStatus.PENDENTE);
        Agendamento tomado = new Agendamento();
        tomado.setStatus(AgendamentoStatus.TOMADO);

        tratamentoMock.getAgendamentos().add(pendente);
        tratamentoMock.getAgendamentos().add(tomado);

        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);
        when(tratamentoRepository.save(any(Tratamento.class))).thenReturn(tratamentoMock);

        tratamentoService.deletarLogico(100, 1);

        assertEquals(TratamentoStatus.CANCELADO, tratamentoMock.getStatus());
        assertEquals(1, tratamentoMock.getAgendamentos().size());
        verify(tratamentoRepository, times(1)).save(tratamentoMock);
    }

    /**
     * Testa se o tratamento é concluído automaticamente quando não restam agendamentos pendentes.
     */
    @Test
    @DisplayName("Deve concluir tratamento se não houver pendências")
    void deveConcluirTratamentoAutomaticamente() {
        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);
        when(agendamentoRepository.countByTratamentoIdAndStatus(100, AgendamentoStatus.PENDENTE)).thenReturn(0L);

        tratamentoService.verificarEConcluirTratamento(100, 1);

        assertEquals(TratamentoStatus.CONCLUIDO, tratamentoMock.getStatus());
        verify(tratamentoRepository, times(1)).save(tratamentoMock);
    }

    /**
     * Testa se o tratamento permanece ATIVO caso ainda existam agendamentos pendentes.
     */
    @Test
    @DisplayName("Não deve concluir tratamento se houver pendências")
    void naoDeveConcluirSeHouverPendencias() {
        when(tratamentoRepository.listarTratamentoPorId(100, 1)).thenReturn(tratamentoMock);
        when(agendamentoRepository.countByTratamentoIdAndStatus(100, AgendamentoStatus.PENDENTE)).thenReturn(5L);

        tratamentoService.verificarEConcluirTratamento(100, 1);

        assertEquals(TratamentoStatus.ATIVO, tratamentoMock.getStatus());
        verify(tratamentoRepository, never()).save(tratamentoMock);
    }
}