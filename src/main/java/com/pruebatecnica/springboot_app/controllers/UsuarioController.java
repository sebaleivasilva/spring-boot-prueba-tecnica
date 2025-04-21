package com.pruebatecnica.springboot_app.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pruebatecnica.springboot_app.dto.UpdateUsuarioRequestDto;
import com.pruebatecnica.springboot_app.dto.UsuarioRequestDto;
import com.pruebatecnica.springboot_app.dto.UsuarioResponseDto;
import com.pruebatecnica.springboot_app.service.UsuarioService;
import com.pruebatecnica.springboot_app.util.JwtUtil;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @RequestMapping(value = "/crearUsuario", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crearUsuario(@RequestBody @Valid UsuarioRequestDto request) {
        UsuarioResponseDto response = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/obtenerUsuario/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable UUID usuarioId, @RequestHeader("Authorization") String token) {
        UUID usuarioIdToken = JwtUtil.validarTokenYObtenerUsuarioId(token.substring(7));

        if (!usuarioIdToken.equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para acceder a los datos de este usuario.");
        }

        UsuarioResponseDto response = usuarioService.obtenerUsuarioPorId(usuarioId);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    @PutMapping(value = "/actualizarUsuario/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizarUsuario(@PathVariable UUID usuarioId, @Valid @RequestBody UsuarioRequestDto request, @RequestHeader("Authorization") String token) {
        UUID usuarioIdToken = JwtUtil.validarTokenYObtenerUsuarioId(token.substring(7));
        if (!usuarioIdToken.equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar los datos de este usuario.");
        }

        UsuarioResponseDto response = usuarioService.actualizarUsuario(usuarioId, request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado para actualizar");
    }

    @DeleteMapping(value = "/eliminarUsuario/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> eliminarUsuario(@PathVariable UUID usuarioId, @RequestHeader("Authorization") String token) {
        UUID usuarioIdToken = JwtUtil.validarTokenYObtenerUsuarioId(token.substring(7));

        if (!usuarioIdToken.equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar los datos de este usuario.");
        }

        boolean eliminado = usuarioService.eliminarUsuario(usuarioId);
        if (eliminado) {
            return ResponseEntity.ok("Usuario eliminado con éxito.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado para eliminar");
    }

    @PatchMapping(value = "/actualizarUsuarioParcial/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizarUsuarioParcial(@PathVariable UUID usuarioId, @Valid @RequestBody UpdateUsuarioRequestDto request, @RequestHeader("Authorization") String token) {
        UUID usuarioIdToken = JwtUtil.validarTokenYObtenerUsuarioId(token.substring(7));
        if (!usuarioIdToken.equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar los datos de este usuario.");
        }

        UsuarioResponseDto response = usuarioService.actualizarUsuarioParcial(usuarioId, request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado para actualizar");
    }
}

