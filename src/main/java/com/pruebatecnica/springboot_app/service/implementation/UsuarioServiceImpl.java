package com.pruebatecnica.springboot_app.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pruebatecnica.springboot_app.entity.Telefono;
import com.pruebatecnica.springboot_app.dto.TelefonoDto;
import com.pruebatecnica.springboot_app.dto.UpdateUsuarioRequestDto;
import com.pruebatecnica.springboot_app.dto.UsuarioRequestDto;
import com.pruebatecnica.springboot_app.dto.UsuarioResponseDto;
import com.pruebatecnica.springboot_app.entity.Usuario;
import com.pruebatecnica.springboot_app.exception.CorreoYaRegistradoException;
import com.pruebatecnica.springboot_app.exception.UsuarioNoEncontradoException;
import com.pruebatecnica.springboot_app.repository.UsuarioRepository;
import com.pruebatecnica.springboot_app.service.UsuarioService;
import com.pruebatecnica.springboot_app.util.JwtUtil;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    private UsuarioRepository usuarioRepository;

    private final ModelMapper modelMapper;

    @Value("${usuario.password.regex}")
    private String passwordRegex;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UsuarioResponseDto crearUsuario(UsuarioRequestDto request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new CorreoYaRegistradoException("El correo ya está registrado");
        }

        if (!Pattern.matches(passwordRegex, request.getContraseña())) {
            throw new IllegalArgumentException("La contraseña no cumple con el formato requerido, debe tener mínimo 8 caracteres, al menos una letra y un número.");
        }

        Usuario usuario = modelMapper.map(request, Usuario.class);

        if (request.getTelefonos() != null && !request.getTelefonos().isEmpty()) {
            List<Telefono> listaTelefonos = request.getTelefonos().stream()
                .map(telefonoDto -> {
                    Telefono telefono = modelMapper.map(telefonoDto, Telefono.class);
                    telefono.setNumero(telefono.getNumero());
                    telefono.setCodigoCiudad(telefono.getCodigoCiudad());
                    telefono.setCodigoPais(telefono.getCodigoPais());
                    telefono.setUsuario(usuario);
                    return telefono;
                })
                .collect(Collectors.toList());
            usuario.setTelefonos(listaTelefonos);            
        }

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setContraseña(request.getContraseña());

        usuarioRepository.save(usuario);

        String token = JwtUtil.generarToken(usuario.getUsuarioId());

        UsuarioResponseDto responseDto = modelMapper.map(usuario, UsuarioResponseDto.class);
        responseDto.setToken(token);
        responseDto.setActivo(usuario.isActivo());
        responseDto.setUltimoLogin(usuario.getUltimoLogin());
        responseDto.setCreado(usuario.getCreado());
        responseDto.setModificado(usuario.getModificado());

        return responseDto;
    }

    @Override
    public UsuarioResponseDto obtenerUsuarioPorId(UUID id) {

        Usuario usuario = usuarioRepository.findByUsuarioId(id);
        UsuarioResponseDto responseDto = modelMapper.map(usuario, UsuarioResponseDto.class);
        responseDto.setActivo(usuario.isActivo());
        responseDto.setUltimoLogin(usuario.getUltimoLogin());
        responseDto.setCreado(usuario.getCreado());
        responseDto.setModificado(usuario.getModificado());

        return responseDto;
    }

    @Override
    public UsuarioResponseDto actualizarUsuario(UUID id, UsuarioRequestDto requestDto) {
        Usuario usuario = usuarioRepository.findByUsuarioId(id);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!usuario.getCorreo().equals(requestDto.getCorreo()) && usuarioRepository.existsByCorreo(requestDto.getCorreo())) {
            throw new CorreoYaRegistradoException("El correo ya está registrado");
        }

        usuario.setNombre(requestDto.getNombre());
        usuario.setCorreo(requestDto.getCorreo());
        usuario.setContraseña(requestDto.getContraseña());
        usuario.setModificado(LocalDateTime.now());

        usuario.getTelefonos().clear();
        if (requestDto.getTelefonos() != null) {
            List<Telefono> telefonosActualizados = requestDto.getTelefonos().stream()
                .map(dto -> {
                    Telefono telefono = new Telefono();
                    telefono.setNumero(dto.getNumero());
                    telefono.setCodigoCiudad(dto.getCodigoCiudad());
                    telefono.setCodigoPais(dto.getCodigoPais());
                    telefono.setUsuario(usuario);
                    return telefono;
                })
                .collect(Collectors.toList());
            usuario.getTelefonos().addAll(telefonosActualizados);
        }

        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioResponseDto.class);
    }

    @Override
    public boolean eliminarUsuario(UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        } else {
            throw new UsuarioNoEncontradoException("El usuario con id " + id + " no fue encontrado.");
        }
    }

    @Override
    public UsuarioResponseDto actualizarUsuarioParcial(UUID usuarioId, UpdateUsuarioRequestDto requestDto) {
        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!usuario.getCorreo().equals(requestDto.getCorreo()) && usuarioRepository.existsByCorreo(requestDto.getCorreo())) {
            throw new CorreoYaRegistradoException("El correo ya está registrado");
        }

         // Actualizar los campos solo si están presentes
         if (requestDto.getNombre() != null) {
            usuario.setNombre(requestDto.getNombre());
        }
        if (requestDto.getCorreo() != null) {
            usuario.setCorreo(requestDto.getCorreo());
        }

        if (requestDto.getContraseña() != null) {
            usuario.setContraseña((requestDto.getContraseña()));
        }

        if (requestDto.getTelefonos() != null) {
            for (TelefonoDto telefonoDto : requestDto.getTelefonos()) {
                Optional<Telefono> telefonoExistenteOpt = usuario.getTelefonos().stream()
                    .filter(t -> t.getNumero().equals(telefonoDto.getNumero()))  // Buscar por número
                    .findFirst();

                if (telefonoExistenteOpt.isPresent()) {
                    Telefono telefonoExistente = telefonoExistenteOpt.get();

                    if (telefonoDto.getCodigoCiudad() != null) {
                        telefonoExistente.setCodigoCiudad(telefonoDto.getCodigoCiudad());
                    }
                    if (telefonoDto.getCodigoPais() != null) {
                        telefonoExistente.setCodigoPais(telefonoDto.getCodigoPais());
                    }
                } else {
                    Telefono nuevoTelefono = new Telefono();
                    nuevoTelefono.setNumero(telefonoDto.getNumero());
                    nuevoTelefono.setCodigoCiudad(telefonoDto.getCodigoCiudad());
                    nuevoTelefono.setCodigoPais(telefonoDto.getCodigoPais());
                    nuevoTelefono.setUsuario(usuario);  // Asociar el teléfono con el usuario
                    usuario.getTelefonos().add(nuevoTelefono);  // Agregar el nuevo teléfono
                }
            }
        }
        usuario.setModificado(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return modelMapper.map(usuario, UsuarioResponseDto.class);

    }

}
