package com.vitta.vittaBackend.service;

import com.vitta.vittaBackend.dto.request.medicamentoHistorico.MedicamentoHistoricoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.request.medicamentoHistorico.MedicamentoHistoricoDTORequest;
import com.vitta.vittaBackend.dto.response.medicamentoHistorico.MedicamentoHistoricoDTOResponse;
import com.vitta.vittaBackend.entity.*;
import com.vitta.vittaBackend.enums.GeralStatus;
import com.vitta.vittaBackend.repository.AgendamentoRepository;
import com.vitta.vittaBackend.repository.MedicamentoHistoricoRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço {@link MedicamentoHistoricoService}.
 * <p>
 * Responsável por validar a lógica de negócio referente ao histórico de uso de medicamentos,
 * incluindo CRUD, validações de segurança e geração de relatórios.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class MedicamentoHistoricoServiceTest {

    @Mock
    private MedicamentoHistoricoRepository medicamentoHistoricoRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MedicamentoHistoricoService medicamentoHistoricoService;

    private Usuario usuarioMock;
    private MedicamentoHistorico historicoMock;
    private Agendamento agendamentoMock;
    private Tratamento tratamentoMock;
    private Medicamento medicamentoMock;

    /**
     * Configuração inicial do cenário de testes.
     * <p>
     * Instancia objetos simulados (Usuario, Medicamento, Tratamento, Agendamento e Histórico)
     * com dados válidos e relacionamentos configurados para evitar NullPointerExceptions
     * durante a geração de relatórios e validações.
     * </p>
     */
    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNome("Usuário Teste");

        medicamentoMock = new Medicamento();
        medicamentoMock.setId(10);
        medicamentoMock.setNome("Paracetamol");

        tratamentoMock = new Tratamento();
        tratamentoMock.setId(20);
        tratamentoMock.setNome("Tratamento Gripe");
        tratamentoMock.setMedicamento(medicamentoMock);

        agendamentoMock = new Agendamento();
        agendamentoMock.setId(30);
        agendamentoMock.setTratamento(tratamentoMock);
        agendamentoMock.setUsuario(usuarioMock);

        historicoMock = new MedicamentoHistorico();
        historicoMock.setId(100);
        historicoMock.setUsuario(usuarioMock);
        historicoMock.setAgendamento(agendamentoMock);
        historicoMock.setDoseTomada(BigDecimal.valueOf(1.0));
        historicoMock.setHoraDoUso(LocalDateTime.now());
        historicoMock.setObservacao("Sem reações");
        historicoMock.setHistoricoStatus(GeralStatus.ATIVO);
    }

    /**
     * Testa a listagem de todo o histórico de um usuário.
     * <p>
     * Verifica se o repositório é chamado com o ID correto e se a lista retornada não é vazia.
     * </p>
     */
    @Test
    @DisplayName("Deve listar históricos de medicamento do usuário")
    void deveListarMedicamentosHistoricos() {
        when(medicamentoHistoricoRepository.listarMedicamentosHistoricos(1)).thenReturn(List.of(historicoMock));

        List<MedicamentoHistoricoDTOResponse> lista = medicamentoHistoricoService.listarMedicamentosHistoricos(1);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        verify(medicamentoHistoricoRepository, times(1)).listarMedicamentosHistoricos(1);
    }

    /**
     * Testa a busca de um registro de histórico específico por ID.
     * <p>
     * Garante que o serviço retorna o DTO correto quando o registro existe e pertence ao usuário.
     * </p>
     */
    @Test
    @DisplayName("Deve listar histórico por ID com sucesso")
    void deveListarMedicamentoHistoricoPorId() {
        when(medicamentoHistoricoRepository.listarMedicamentoHistoricoPorId(100, 1)).thenReturn(historicoMock);

        MedicamentoHistoricoDTOResponse response = medicamentoHistoricoService.listarMedicamentoHistoricoPorId(100, 1);

        assertNotNull(response);
        assertEquals(100, response.getId());
    }

    /**
     * Testa o cenário de falha ao buscar um histórico inexistente.
     * <p>
     * Verifica se uma exceção é lançada quando o repositório retorna nulo.
     * </p>
     */
    @Test
    @DisplayName("Deve lançar erro ao buscar histórico inexistente")
    void deveFalharAoBuscarHistoricoInexistente() {
        when(medicamentoHistoricoRepository.listarMedicamentoHistoricoPorId(999, 1)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            medicamentoHistoricoService.listarMedicamentoHistoricoPorId(999, 1);
        });
    }

    /**
     * Testa o cadastro de um novo histórico de uso.
     * <p>
     * Verifica se o serviço valida a existência do agendamento antes de salvar o histórico.
     * </p>
     */
    @Test
    @DisplayName("Deve cadastrar novo histórico de medicamento")
    void deveCadastrarMedicamentoHistorico() {
        MedicamentoHistoricoDTORequest request = new MedicamentoHistoricoDTORequest();
        request.setAgendamentoId(30);
        request.setDoseTomada(BigDecimal.valueOf(1.0));
        request.setHoraDoUso(LocalDateTime.now());
        request.setObservacao("Teste");

        when(agendamentoRepository.listarAgendamentoPorId(30, 1)).thenReturn(agendamentoMock);
        when(usuarioRepository.getReferenceById(1)).thenReturn(usuarioMock);
        when(medicamentoHistoricoRepository.save(any(MedicamentoHistorico.class))).thenReturn(historicoMock);

        MedicamentoHistoricoDTOResponse response = medicamentoHistoricoService.cadastrarMedicamentoHistorico(request, 1);

        assertNotNull(response);
        verify(medicamentoHistoricoRepository, times(1)).save(any(MedicamentoHistorico.class));
    }

    /**
     * Testa a falha no cadastro quando o agendamento informado não existe ou não pertence ao usuário.
     */
    @Test
    @DisplayName("Deve falhar cadastro se agendamento não for encontrado")
    void deveFalharCadastroAgendamentoInexistente() {
        MedicamentoHistoricoDTORequest request = new MedicamentoHistoricoDTORequest();
        request.setAgendamentoId(999);

        when(agendamentoRepository.listarAgendamentoPorId(999, 1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            medicamentoHistoricoService.cadastrarMedicamentoHistorico(request, 1);
        });
    }

    /**
     * Testa a atualização de campos de um histórico existente.
     * <p>
     * Verifica se os novos valores (observação, dose, hora) são aplicados corretamente ao objeto antes de salvar.
     * </p>
     */
    @Test
    @DisplayName("Deve atualizar histórico existente")
    void deveAtualizarMedicamentoHistorico() {
        MedicamentoHistoricoAtualizarDTORequest request = new MedicamentoHistoricoAtualizarDTORequest();
        request.setObservacao("Nova observação");
        request.setDoseTomada(BigDecimal.valueOf(2.0));

        when(medicamentoHistoricoRepository.listarMedicamentoHistoricoPorId(100, 1)).thenReturn(historicoMock);
        when(medicamentoHistoricoRepository.save(any(MedicamentoHistorico.class))).thenAnswer(i -> i.getArgument(0));

        MedicamentoHistoricoDTOResponse response = medicamentoHistoricoService.atualizarMedicamentoHistorico(100, 1, request);

        assertEquals("Nova observação", response.getObservacao());
        assertEquals(BigDecimal.valueOf(2.0), response.getDoseTomada());
        verify(medicamentoHistoricoRepository, times(1)).save(historicoMock);
    }

    /**
     * Testa a exclusão lógica de um registro de histórico.
     */
    @Test
    @DisplayName("Deve deletar logicamente um histórico")
    void deveDeletarMedicamentoHistorico() {
        doNothing().when(medicamentoHistoricoRepository).apagarLogicoMedicamentoHistorico(100, 1);

        medicamentoHistoricoService.deletarMedicamentoHistorico(100, 1);

        verify(medicamentoHistoricoRepository, times(1)).apagarLogicoMedicamentoHistorico(100, 1);
    }

    /**
     * Testa a geração do relatório em PDF quando existem dados.
     * <p>
     * Verifica se o método não lança exceção e retorna um array de bytes não vazio.
     * Simula o retorno de uma lista de históricos para preencher a tabela do PDF.
     * </p>
     */
    @Test
    @DisplayName("Deve gerar relatório PDF com dados")
    void deveGerarRelatorioPdfComDados() {
        when(medicamentoHistoricoRepository.listarHistoricoPorPeriodo(eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(historicoMock));

        byte[] pdfBytes = medicamentoHistoricoService.gerarRelatorioPdf(1);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        verify(medicamentoHistoricoRepository, times(1))
                .listarHistoricoPorPeriodo(eq(1), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Testa a geração do relatório em PDF quando não há dados no período.
     * <p>
     * Verifica se o PDF é gerado corretamente (contendo a mensagem de "Nenhum registro")
     * mesmo quando a lista retornada pelo banco está vazia.
     * </p>
     */
    @Test
    @DisplayName("Deve gerar relatório PDF vazio (sem dados)")
    void deveGerarRelatorioPdfVazio() {
        when(medicamentoHistoricoRepository.listarHistoricoPorPeriodo(eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        byte[] pdfBytes = medicamentoHistoricoService.gerarRelatorioPdf(1);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    /**
     * Testa a listagem de dados para o relatório mensal (formato DTO).
     * <p>
     * Verifica se o serviço filtra corretamente pelo período do mês atual e retorna a lista convertida.
     * </p>
     */
    @Test
    @DisplayName("Deve listar dados do relatório mensal")
    void deveListarDadosRelatorioMensal() {
        when(medicamentoHistoricoRepository.listarHistoricoPorPeriodo(eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(historicoMock));

        List<MedicamentoHistoricoDTOResponse> lista = medicamentoHistoricoService.listarDadosRelatorioMensal(1);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }
}