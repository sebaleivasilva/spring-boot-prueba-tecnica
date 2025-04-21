package com.pruebatecnica.springboot_app.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pruebatecnica.springboot_app.entity.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>{

    boolean existsByCorreo(String correo);
    
    Usuario findByUsuarioId(UUID id);
}
