package com.vitta.vittaBackend.dto.response.usuario;

import com.vitta.vittaBackend.entity.Usuario;
import com.vitta.vittaBackend.enums.UsuarioStatus;

/**
 * DTO para representar uma visão simplificada de um Usuário.
 * Ideal para ser aninhado em outras respostas de DTO (ex: dentro de um Tratamento)
 * para fornecer contexto sobre o usuário sem carregar dados excessivos ou criar referências circulares.
 */
public class UsuarioResumoDTOResponse {

    /**
     * O ID único do usuário.
     */
    private Integer id;

    /**
     * O nome completo do usuário.
     */
    private String nome;

    /**
     * O status atual da conta do usuário (ex: ATIVO, INATIVO).
     */
    private UsuarioStatus status;

    /**
     * Construtor vazio.
     * Utilizado por frameworks de serialização/deserialização.
     */
    public UsuarioResumoDTOResponse() {
    }

    /**
     * Construtor de mapeamento a partir da entidade.
     * Facilita a conversão da entidade {@link Usuario} para este DTO de resumo.
     * @param usuarioEntity A entidade Usuário vinda do banco de dados.
     */
    public UsuarioResumoDTOResponse(Usuario usuarioEntity) {
        this.id = usuarioEntity.getId();
        this.nome = usuarioEntity.getNome();
        this.status = usuarioEntity.getStatus();
    }

    public UsuarioResumoDTOResponse(Integer id, String nome, UsuarioStatus status) {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UsuarioStatus getStatus() {
        return status;
    }

    public void setStatus(UsuarioStatus status) {
        this.status = status;
    }
}
