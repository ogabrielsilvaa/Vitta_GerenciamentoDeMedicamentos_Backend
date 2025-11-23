package com.vitta.vittaBackend.service;

import com.vitta.vittaBackend.dto.request.medicamento.MedicamentoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.request.medicamento.MedicamentoDTORequest;
import com.vitta.vittaBackend.dto.response.medicamento.MedicamentoDTOResponse;
import com.vitta.vittaBackend.entity.Medicamento;
import com.vitta.vittaBackend.entity.Usuario;
import com.vitta.vittaBackend.enums.GeralStatus;
import com.vitta.vittaBackend.enums.medicamento.TipoUnidadeDeMedida;
import com.vitta.vittaBackend.repository.MedicamentoRepository;
import com.vitta.vittaBackend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço {@link MedicamentoService}.
 * <p>
 * Utiliza o framework Mockito para simular o comportamento dos repositórios
 * e isolar a lógica de negócio durante os testes.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MedicamentoService medicamentoService;

    private Usuario usuarioMock;
    private Medicamento medicamentoMock;
    private MedicamentoDTORequest medicamentoDTORequest;

    /**
     * Configuração inicial executada antes de cada método de teste.
     * <p>
     * Inicializa os objetos mocks (Usuario, Medicamento e DTORequest) com dados padrão
     * para serem utilizados nos cenários de teste.
     * </p>
     */
    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNome("Usuário Teste");

        medicamentoMock = new Medicamento();
        medicamentoMock.setId(10);
        medicamentoMock.setNome("Dipirona");
        medicamentoMock.setPrincipioAtivo("Metamizol");
        medicamentoMock.setLaboratorio("Medley");
        medicamentoMock.setTipoUnidadeDeMedida(TipoUnidadeDeMedida.MG);
        medicamentoMock.setStatus(GeralStatus.ATIVO);
        medicamentoMock.setUsuario(usuarioMock);

        medicamentoDTORequest = new MedicamentoDTORequest();
        medicamentoDTORequest.setNome("Dipirona");
        medicamentoDTORequest.setPrincipioAtivo("Metamizol");
        medicamentoDTORequest.setLaboratorio("Medley");
        medicamentoDTORequest.setTipoUnidadeDeMedida(1);
    }

    /**
     * Testa o cadastro bem-sucedido de um medicamento.
     * <p>
     * Verifica se o serviço chama o repositório corretamente e se o objeto retornado
     * contém os dados esperados.
     * </p>
     */
    @Test
    @DisplayName("Deve cadastrar um medicamento com sucesso")
    void deveCadastrarMedicamento() {
        when(usuarioRepository.getReferenceById(1)).thenReturn(usuarioMock);
        when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamentoMock);

        MedicamentoDTOResponse response = medicamentoService.cadastrarMedicamento(medicamentoDTORequest, 1);

        assertNotNull(response);
        assertEquals("Dipirona", response.getNome());
        verify(medicamentoRepository, times(1)).save(any(Medicamento.class));
    }

    /**
     * Testa a listagem de medicamentos ativos vinculados a um usuário específico.
     * <p>
     * Verifica se a lista retornada não está vazia e se contém os itens esperados.
     * </p>
     */
    @Test
    @DisplayName("Deve listar medicamentos por usuário")
    void deveListarMedicamentosPorUsuario() {
        when(medicamentoRepository.listarMedicamentos(1)).thenReturn(List.of(medicamentoMock));

        List<MedicamentoDTOResponse> lista = medicamentoService.listarMedicamentosPorUsuario(1);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("Dipirona", lista.get(0).getNome());
        verify(medicamentoRepository, times(1)).listarMedicamentos(1);
    }

    /**
     * Testa a busca de um medicamento específico pelo seu ID e ID do usuário.
     * <p>
     * Verifica se o serviço retorna o DTO correto quando o medicamento existe.
     * </p>
     */
    @Test
    @DisplayName("Deve buscar medicamento por ID com sucesso")
    void deveBuscarMedicamentoPorId() {
        when(medicamentoRepository.listarMedicamentoPorId(10, 1)).thenReturn(medicamentoMock);

        MedicamentoDTOResponse response = medicamentoService.listarMedicamentoPorId(10, 1);

        assertNotNull(response);
        assertEquals(10, response.getId());
    }

    /**
     * Testa o cenário de erro ao tentar buscar um medicamento inexistente ou pertencente a outro usuário.
     * <p>
     * Espera-se que uma exceção {@link EntityNotFoundException} seja lançada.
     * </p>
     */
    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente ou de outro usuário")
    void deveFalharAoBuscarMedicamentoInexistente() {
        when(medicamentoRepository.listarMedicamentoPorId(99, 1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            medicamentoService.listarMedicamentoPorId(99, 1);
        });
    }

    /**
     * Testa a atualização de dados de um medicamento existente.
     * <p>
     * Verifica se as alterações solicitadas (ex: nome) são aplicadas corretamente,
     * mantendo os dados não alterados (ex: princípio ativo).
     * </p>
     */
    @Test
    @DisplayName("Deve atualizar medicamento com sucesso")
    void deveAtualizarMedicamento() {
        MedicamentoAtualizarDTORequest updateDTO = new MedicamentoAtualizarDTORequest();
        updateDTO.setNome("Dipirona Atualizada");

        when(medicamentoRepository.listarMedicamentoPorId(10, 1)).thenReturn(medicamentoMock);
        when(medicamentoRepository.save(any(Medicamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicamentoDTOResponse response = medicamentoService.atualizarMedicamento(10, 1, updateDTO);

        assertEquals("Dipirona Atualizada", response.getNome());
        assertEquals("Metamizol", response.getPrincipioAtivo());
        verify(medicamentoRepository, times(1)).save(medicamentoMock);
    }

    /**
     * Testa a exclusão lógica (soft delete) de um medicamento.
     * <p>
     * Verifica se o metodo correto do repositório é chamado para alterar o status do medicamento.
     * </p>
     */
    @Test
    @DisplayName("Deve realizar exclusão lógica (deletarLogico)")
    void deveDeletarLogico() {
        when(medicamentoRepository.listarMedicamentoPorId(10, 1)).thenReturn(medicamentoMock);

        doNothing().when(medicamentoRepository).apagarLogicoMedicamento(10, 1);

        medicamentoService.deletarLogico(10, 1);

        verify(medicamentoRepository, times(1)).apagarLogicoMedicamento(10, 1);
    }

    /**
     * Testa o cenário de erro ao tentar deletar um medicamento que não existe.
     * <p>
     * Espera-se que uma {@link EntityNotFoundException} seja lançada e que o método
     * de exclusão do repositório nunca seja chamado.
     * </p>
     */
    @Test
    @DisplayName("Deve lançar erro ao tentar deletar medicamento que não existe")
    void deveFalharAoDeletarMedicamentoInexistente() {
        when(medicamentoRepository.listarMedicamentoPorId(99, 1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            medicamentoService.deletarLogico(99, 1);
        });

        verify(medicamentoRepository, never()).apagarLogicoMedicamento(anyInt(), anyInt());
    }

    /**
     * Testa a listagem de medicamentos inativos de um usuário.
     * <p>
     * Verifica se o serviço retorna corretamente a lista de itens com status inativo.
     * </p>
     */
    @Test
    @DisplayName("Deve listar medicamentos inativos")
    void deveListarMedicamentosInativos() {
        Medicamento inativo = new Medicamento();
        inativo.setStatus(GeralStatus.INATIVO);

        when(medicamentoRepository.listarMedicamentosInativos(1)).thenReturn(List.of(inativo));

        List<MedicamentoDTOResponse> lista = medicamentoService.listarMedicamentosInativos(1);

        assertFalse(lista.isEmpty());
        verify(medicamentoRepository, times(1)).listarMedicamentosInativos(1);
    }
}