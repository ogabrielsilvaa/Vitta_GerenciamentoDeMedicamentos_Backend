package com.vitta.vittaBackend.dto.security;

public record UsuarioLoginDto(
        String email,
        String senha
) {
}
