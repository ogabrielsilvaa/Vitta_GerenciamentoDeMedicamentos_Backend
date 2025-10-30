package com.vitta.vittaBackend.dto.response.usuario;

import com.vitta.vittaBackend.enums.UsuarioStatus;

public class UsuarioLoginDTOResponse {
    private Integer id;
    private String nome;
    private String telefone;
    private String email;
    private UsuarioStatus status;

    public UsuarioLoginDTOResponse() {
    }

    public UsuarioLoginDTOResponse(Integer id, String nome, String telefone, String email, UsuarioStatus status) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.status = status;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UsuarioStatus getStatus() {
        return status;
    }

    public void setStatus(UsuarioStatus status) {
        this.status = status;
    }
}
