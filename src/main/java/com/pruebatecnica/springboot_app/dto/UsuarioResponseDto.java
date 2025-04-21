package com.pruebatecnica.springboot_app.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class UsuarioResponseDto {

    private UUID id;

    private LocalDateTime creado;

    private LocalDateTime modificado;

    private LocalDateTime ultimoLogin;

    private String token;

    private boolean activo;

}
