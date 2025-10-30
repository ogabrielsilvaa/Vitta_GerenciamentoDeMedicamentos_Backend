package com.vitta.vittaBackend.dto.security;

import com.vitta.vittaBackend.dto.response.usuario.UsuarioDTOResponse;
import com.vitta.vittaBackend.dto.response.usuario.UsuarioLoginDTOResponse;
import com.vitta.vittaBackend.dto.response.usuario.UsuarioResumoDTOResponse;

public record RecoveryJwtTokenDto(
        String token,
        UsuarioLoginDTOResponse usuario
) {
}
