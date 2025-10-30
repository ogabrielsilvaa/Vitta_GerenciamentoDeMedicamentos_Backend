package com.vitta.vittaBackend.service;

import com.vitta.vittaBackend.dto.request.usuario.UsuarioDTORequest;
import com.vitta.vittaBackend.dto.request.usuario.UsuarioAtualizarDTORequest;
import com.vitta.vittaBackend.dto.response.usuario.*;
import com.vitta.vittaBackend.dto.security.UsuarioLoginDto;
import com.vitta.vittaBackend.dto.security.RecoveryJwtTokenDto;
import com.vitta.vittaBackend.entity.Role;
import com.vitta.vittaBackend.entity.Usuario;
import com.vitta.vittaBackend.enums.UsuarioStatus;
import com.vitta.vittaBackend.enums.security.RoleName;
import com.vitta.vittaBackend.repository.UsuarioRepository;
import com.vitta.vittaBackend.repository.security.RoleRepository;
import com.vitta.vittaBackend.security.UserDetailsImpl;
import com.vitta.vittaBackend.service.security.JwtTokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Camada de serviço para gerenciar a lógica de negócio dos Usuários.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenService jwtTokenService;
    private RoleRepository roleRepository;

    @Autowired
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            RoleRepository roleRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.roleRepository = roleRepository;
    }

    /**
     * Busca o perfil completo do usuário autenticado.
     * @param usuarioId O ID do usuário autenticado (vindo do token).
     * @return O {@link UsuarioDTOResponse} correspondente ao seu perfil.
     */
    public UsuarioDTOResponse buscarMeuPerfil(Integer usuarioId) {
        Usuario usuario = validarUsuario(usuarioId);
        return new UsuarioDTOResponse(usuario);
    }

    /**
     * Busca um usuário pelo seu ID e retorna seus agendamentos.
     *
     * @param usuarioId O ID único do usuário a ser buscado.
     * @return um {@link UsuarioAgendamentosDTOResponse} contendo a lista de agendamentos do usuário.
     * @throws RuntimeException se o usuário com o ID fornecido não for encontrado.
     */
    public UsuarioAgendamentosDTOResponse buscarMeusAgendamentos(Integer usuarioId) {
        Usuario usuario = validarUsuario(usuarioId);
        return new UsuarioAgendamentosDTOResponse(usuario);
    }

    /**
     * Busca um usuário pelo seu ID e retorna seu histórico de uso de medicamentos.
     *
     * @param usuarioId O ID único do usuário a ser buscado.
     * @return um {@link UsuarioHistoricoDTOResponse} contendo o histórico de medicamentos do usuário.
     * @throws RuntimeException se o usuário com o ID fornecido não for encontrado.
     */
    public UsuarioHistoricoDTOResponse buscarMeusHistoricos(Integer usuarioId) {
        Usuario usuario = validarUsuario(usuarioId);
        return new UsuarioHistoricoDTOResponse(usuario);
    }

    /**
     * Busca um usuário pelo seu ID e retorna sua lista de medicamentos cadastrados.
     *
     * @param usuarioId O ID único do usuário a ser buscado.
     * @return um {@link UsuarioMedicamentosDTOResponse} contendo a lista de medicamentos do usuário.
     * @throws RuntimeException se o usuário com o ID fornecido não for encontrado.
     */
    public UsuarioMedicamentosDTOResponse buscarMeusMedicamentos(Integer usuarioId) {
        Usuario usuario = validarUsuario(usuarioId);
        return new UsuarioMedicamentosDTOResponse(usuario);
    }

    /**
     * Busca um usuário pelo seu ID e retorna seus tratamentos ativos.
     *
     * @param usuarioId O ID único do usuário a ser buscado.
     * @return um {@link UsuarioTratamentosDTOResponse} contendo a lista de tratamentos do usuário.
     * @throws RuntimeException se o usuário com o ID fornecido não for encontrado.
     */
    public UsuarioTratamentosDTOResponse buscarMeusTratamentos(Integer usuarioId) {
        Usuario usuario = validarUsuario(usuarioId);
        return new UsuarioTratamentosDTOResponse(usuario);
    }

    /**
     * Cria um novo usuário no sistema com a permissão padrão de cliente.
     * Este método faz parte do fluxo de segurança e registro.
     *
     * @param usuarioDTORequest DTO contendo os dados (nome, telefone, email, senha) do novo usuário.
     * @throws RuntimeException se já existir um usuário cadastrado com o mesmo e-mail,
     * ou se a role padrão 'ROLE_CUSTOMER' não for encontrada no sistema.
     */
    public void criarUsuario(UsuarioDTORequest usuarioDTORequest) {

        if (usuarioRepository.findByEmail(usuarioDTORequest.getEmail()).isPresent()) {
            throw new RuntimeException("Usuário com este email já existe");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(usuarioDTORequest.getNome());
        novoUsuario.setTelefone(usuarioDTORequest.getTelefone());
        novoUsuario.setEmail(usuarioDTORequest.getEmail().toLowerCase());

        novoUsuario.setSenha(passwordEncoder.encode(usuarioDTORequest.getSenha()));

        /// já define o novo usuario que será criado com a role customer, sem precisar passar no request
        Role rolePadrao = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Erro crítico: A role padrão 'ROLE_CUSTOMER' não foi encontrada no banco."));

        novoUsuario.setRoles(Collections.singletonList(rolePadrao));

        usuarioRepository.save(novoUsuario);
    }

    /**
     * Autentica um usuário com base em suas credenciais e gera um token de acesso JWT.
     * Este método faz parte do fluxo de segurança e login.
     *
     * @param usuarioLoginDTO DTO contendo o e-mail e a senha para autenticação.
     * @return um {@link RecoveryJwtTokenDto} contendo o token JWT gerado para o usuário autenticado.
     * @throws org.springframework.security.core.AuthenticationException se as credenciais forem inválidas.
     * @throws RuntimeException se o usuário com o e-mail fornecido não for encontrado após a autenticação.
     */
    public RecoveryJwtTokenDto autenticarUsuario(UsuarioLoginDto usuarioLoginDTO) {

        // Autentica o usuário
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioLoginDTO.email().toLowerCase(), usuarioLoginDTO.senha())
        );

        // Busca usuário no banco
        Usuario usuario = usuarioRepository.findByEmail(usuarioLoginDTO.email().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gera token JWT
        String token = jwtTokenService.generateToken(new UserDetailsImpl(usuario));

        UsuarioLoginDTOResponse usuarioDTO = new UsuarioLoginDTOResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getTelefone(),
                usuario.getEmail(),
                usuario.getStatus()
        );

        return new RecoveryJwtTokenDto(token, usuarioDTO);
    }

    /**
     * Atualiza os dados de um usuário existente (nome e telefone).
     * @param usuarioId O ID do usuário autenticado (vindo do token).
     * @param usuarioAtualizarDTORequest O DTO com os novos dados.
     * @return O {@link UsuarioDTOResponse} da entidade atualizada.
     */
    @Transactional
    public UsuarioDTOResponse atualizarMeuPerfil(Integer usuarioId, UsuarioAtualizarDTORequest usuarioAtualizarDTORequest) {
        Usuario usuarioExistente = validarUsuario(usuarioId);

        if (usuarioAtualizarDTORequest.getNome() != null) {
            usuarioExistente.setNome(usuarioAtualizarDTORequest.getNome());
        }
        if (usuarioAtualizarDTORequest.getTelefone() != null) {
            usuarioExistente.setTelefone(usuarioAtualizarDTORequest.getTelefone());
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return new UsuarioDTOResponse(usuarioAtualizado);
    }

    /**
     * Realiza a exclusão lógica da conta do usuário autenticado.
     * @param usuarioId O ID do usuário autenticado (vindo do token).
     */
    @Transactional
    public void deletarMinhaConta(Integer usuarioId) {
        Usuario usuario = validarUsuario(usuarioId);

        usuario.setStatus(UsuarioStatus.INATIVO);
        usuarioRepository.save(usuario);
    }

    /**
     * Valida a existência de um usuário pelo seu ID e o retorna.
     * Este é um método auxiliar privado para evitar a repetição de código nos
     * métodos públicos que precisam de buscar uma entidade antes de realizar uma ação.
     *
     * @param usuarioId O ID do usuário a ser validado e buscado (vindo do token).
     * @return A entidade {@link Usuario} encontrada.
     */
    private Usuario validarUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepository.listarUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuário não encontrado com o ID: " + usuarioId);
        }
        return usuario;
    }

    //    /**
//     * Retorna uma lista de todos os usuários com status ATIVO.
//     * @return Uma lista de {@link UsuarioDTOResponse}.
//     */
//    public List<UsuarioDTOResponse> listarUsuariosAtivos() {
//        return usuarioRepository.listarUsuariosAtivos()
//                .stream()
//                .map(UsuarioDTOResponse::new)
//                .collect(Collectors.toList());
//    }

}
