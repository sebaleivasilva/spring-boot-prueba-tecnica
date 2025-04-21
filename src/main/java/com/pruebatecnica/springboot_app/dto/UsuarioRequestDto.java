package com.pruebatecnica.springboot_app.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsuarioRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @NotNull(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @NotNull(message = "El correo es obligatorio")
    @Pattern(
    regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
    message = "El correo debe tener el formato aaaaaaa@dominio.cl"
    )
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @NotNull(message = "La contraseña es obligatoria")
    private String contraseña;
    
    private List<TelefonoDto> telefonos;

}
