package com.pruebatecnica.springboot_app.dto;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUsuarioRequestDto {
    
    private String nombre;

    @Pattern(
    regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
    message = "El correo debe tener el formato aaaaaaa@dominio.cl"
    )
    private String correo;

    private String contraseña;
    
    private List<TelefonoDto> telefonos;

}
