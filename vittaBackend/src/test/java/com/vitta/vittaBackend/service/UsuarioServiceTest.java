package com.vitta.vittaBackend.service;

import com.vitta.vittaBackend.dto.request.usuario.UsuarioAtualizarDTORequest;
import com.vitta.vittaBackend.dto.request.usuario.UsuarioDTORequest;
import com.vitta.vittaBackend.dto.response.usuario.*;
import com.vitta.vittaBackend.dto.security.RecoveryJwtTokenDto;
import com.vitta.vittaBackend.dto.security.UsuarioLoginDto;
import com.vitta.vittaBackend.entity.Role;
import com.vitta.vittaBackend.entity.Usuario;
import com.vitta.vittaBackend.enums.UsuarioStatus;
import com.vitta.vittaBackend.enums.security.RoleName;
import com.vitta.vittaBackend.repository.UsuarioRepository;
import com.vitta.vittaBackend.repository.security.RoleRepository;
import com.vitta.vittaBackend.security.UserDetailsImpl;
import com.vitta.vittaBackend.service.security.JwtTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço {@link UsuarioService}.
 * <p>
 * Verifica as regras de negócio relacionadas à gestão de usuários, incluindo
 * registro, login (autenticação), recuperação de dados e manipulação de perfil.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;
    private Role roleCliente;

    /**
     * Configuração inicial executada antes de cada teste.
     * Inicializa objetos comuns como Usuário e Role para uso nos cenários.
     */
    @BeforeEach
    void setUp() {
        roleCliente = new Role();
        roleCliente.setId(1L);
        roleCliente.setName(RoleName.ROLE_CUSTOMER);

        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNome("João Silva");
        usuarioMock.setEmail("joao@email.com");
        usuarioMock.setSenha("senhaCodificada");
        usuarioMock.setTelefone("11999999999");
        usuarioMock.setStatus(UsuarioStatus.ATIVO);
        usuarioMock.setRoles(new ArrayList<>());
        usuarioMock.getRoles().add(roleCliente);

        usuarioMock.setAgendamentos(new ArrayList<>());
        usuarioMock.setMedicamentos(new ArrayList<>());
        usuarioMock.setTratamentos(new ArrayList<>());
        usuarioMock.setHistoricos(new ArrayList<>());
    }

    /**
     * Testa o cenário de busca de perfil com sucesso.
     */
    @Test
    @DisplayName("Deve buscar perfil do usuário com sucesso")
    void deveBuscarMeuPerfil() {
        when(usuarioRepository.listarUsuarioPorId(1)).thenReturn(usuarioMock);

        UsuarioDTOResponse response = usuarioService.buscarMeuPerfil(1);

        assertNotNull(response);
        assertEquals(usuarioMock.getId(), response.getId());
        assertEquals(usuarioMock.getNome(), response.getNome());
    }

    /**
     * Testa a falha ao buscar perfil de um usuário inexistente.
     */
    @Test
    @DisplayName("Deve lançar exceção ao buscar perfil de usuário inexistente")
    void deveFalharBuscarPerfilInexistente() {
        when(usuarioRepository.listarUsuarioPorId(99)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            usuarioService.buscarMeuPerfil(99);
        });
    }

    /**
     * Testa o método de busca de agendamentos do usuário.
     */
    @Test
    @DisplayName("Deve buscar agendamentos do usuário")
    void deveBuscarMeusAgendamentos() {
        when(usuarioRepository.listarUsuarioPorId(1)).thenReturn(usuarioMock);

        UsuarioAgendamentosDTOResponse response = usuarioService.buscarMeusAgendamentos(1);

        assertNotNull(response);
    }

    /**
     * Testa o método de busca de históricos do usuário.
     */
    @Test
    @DisplayName("Deve buscar históricos do usuário")
    void deveBuscarMeusHistoricos() {
        when(usuarioRepository.listarUsuarioPorId(1)).thenReturn(usuarioMock);
        UsuarioHistoricoDTOResponse response = usuarioService.buscarMeusHistoricos(1);
        assertNotNull(response);
    }

    /**
     * Testa o método de busca de medicamentos do usuário.
     */
    @Test
    @DisplayName("Deve buscar medicamentos do usuário")
    void deveBuscarMeusMedicamentos() {
        when(usuarioRepository.listarUsuarioPorId(1)).thenReturn(usuarioMock);
        UsuarioMedicamentosDTOResponse response = usuarioService.buscarMeusMedicamentos(1);
        assertNotNull(response);
    }

    /**
     * Testa o método de busca de tratamentos do usuário.
     */
    @Test
    @DisplayName("Deve buscar tratamentos do usuário")
    void deveBuscarMeusTratamentos() {
        when(usuarioRepository.listarUsuarioPorId(1)).thenReturn(usuarioMock);
        UsuarioTratamentosDTOResponse response = usuarioService.buscarMeusTratamentos(1);
        assertNotNull(response);
    }

    /**
     * Testa a criação de um novo usuário com sucesso.
     * Verifica criptografia de senha e atribuição de role.
     */
    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuario() {
        UsuarioDTORequest request = new UsuarioDTORequest();
        request.setNome("Maria");
        request.setEmail("maria@email.com");
        request.setSenha("123456");
        request.setTelefone("11888888888");

        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashSenha");
        when(roleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(roleCliente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        usuarioService.criarUsuario(request);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode("123456");
    }

    /**
     * Testa a falha na criação de usuário quando o e-mail já existe.
     */
    @Test
    @DisplayName("Deve falhar ao criar usuário com email existente")
    void deveFalharCriarUsuarioEmailDuplicado() {
        UsuarioDTORequest request = new UsuarioDTORequest();
        request.setEmail("joao@email.com");

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuarioMock));

        assertThrows(RuntimeException.class, () -> {
            usuarioService.criarUsuario(request);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /**
     * Testa a autenticação (login) bem-sucedida.
     * Verifica se o token JWT é gerado.
     */
    @Test
    @DisplayName("Deve autenticar usuário e gerar token")
    void deveAutenticarUsuario() {
        UsuarioLoginDto loginDto = new UsuarioLoginDto("joao@email.com", "senha123");

        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuarioMock));
        when(jwtTokenService.generateToken(any(UserDetailsImpl.class))).thenReturn("token.jwt.falso");

        RecoveryJwtTokenDto response = usuarioService.autenticarUsuario(loginDto);

        assertNotNull(response);
        assertEquals("token.jwt.falso", response.token());
    }

    /**
     * Testa a atualização de dados do perfil do usuário.
     */
    @Test
    @DisplayName("Deve atualizar perfil do usuário")
    void deveAtualizarMeuPerfil() {
        UsuarioAtualizarDTORequest req = new UsuarioAtualizarDTORequest();
        req.setNome("João da Silva Atualizado");
        req.setTelefone("11900000000");

        when(usuarioRepository.listarUsuarioPorId(1)).thenReturn(usuarioMock);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioDTOResponse response = usuarioService.atualizarMeuPerfil(1, req);

        assertEquals("João da Silva Atualizado", response.getNome());
        assertEquals("11900000000", response.getTelefone());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    /**
     * Testa a exclusão lógica da conta do usuário.
     */
    @Test
    @DisplayName("Deve deletar conta logicamente")
    void deveDeletarMinhaConta() {
        when(usuarioRepository.listarUsuarioPorId(1)).thenReturn(usuarioMock);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        usuarioService.deletarMinhaConta(1);

        assertEquals(UsuarioStatus.INATIVO, usuarioMock.getStatus());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }
}