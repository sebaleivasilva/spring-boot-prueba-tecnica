package com.pruebatecnica.springboot_app.service;

import java.util.UUID;

import com.pruebatecnica.springboot_app.dto.UpdateUsuarioRequestDto;
import com.pruebatecnica.springboot_app.dto.UsuarioRequestDto;
import com.pruebatecnica.springboot_app.dto.UsuarioResponseDto;

public interface UsuarioService {

    public UsuarioResponseDto crearUsuario(UsuarioRequestDto request);

    public UsuarioResponseDto obtenerUsuarioPorId(UUID id);

    public UsuarioResponseDto actualizarUsuario(UUID id, UsuarioRequestDto requestDto);

    public boolean eliminarUsuario(UUID id);

    public UsuarioResponseDto actualizarUsuarioParcial(UUID usuarioId, UpdateUsuarioRequestDto request);
}