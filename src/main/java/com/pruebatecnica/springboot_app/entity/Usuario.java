package com.pruebatecnica.springboot_app.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID usuarioId;

    private String nombre;

    private String correo;

    private String contraseña;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefono> telefonos;

    private boolean activo = true;

    private LocalDateTime creado;

    private LocalDateTime modificado;

    private LocalDateTime ultimoLogin;

    @PrePersist
    protected void onCreate() {
        this.creado = LocalDateTime.now();
        this.ultimoLogin = this.creado;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modificado = LocalDateTime.now();
    }

}
