package com.pruebatecnica.springboot_app.service.implementation;

import com.pruebatecnica.springboot_app.dto.UsuarioRequestDto;
import com.pruebatecnica.springboot_app.dto.UsuarioResponseDto;
import com.pruebatecnica.springboot_app.entity.Telefono;
import com.pruebatecnica.springboot_app.entity.Usuario;
import com.pruebatecnica.springboot_app.exception.CorreoYaRegistradoException;
import com.pruebatecnica.springboot_app.exception.UsuarioNoEncontradoException;
import com.pruebatecnica.springboot_app.repository.UsuarioRepository;
import com.pruebatecnica.springboot_app.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ModelMapper modelMapper;

    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, modelMapper);

        ReflectionTestUtils.setField(usuarioService, "passwordRegex", "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");        
    }

    @Test
    public void testCrearUsuario() {
        UsuarioRequestDto usuarioRequestDto = new UsuarioRequestDto();
        usuarioRequestDto.setNombre("Juan Perez");
        usuarioRequestDto.setCorreo("juan.perez@dominio.cl");
        usuarioRequestDto.setContraseña("juanjuan123");

        Telefono telefono = new Telefono();
        telefono.setNumero("123456789");
        telefono.setCodigoCiudad("9");
        telefono.setCodigoPais("56");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsuarioId(UUID.randomUUID());
        usuarioMock.setCorreo(usuarioRequestDto.getCorreo());
        usuarioMock.setContraseña(usuarioRequestDto.getContraseña());
        usuarioMock.setNombre(usuarioRequestDto.getNombre());
        usuarioMock.setTelefonos(new ArrayList<>());
        usuarioMock.getTelefonos().add(telefono);

        UsuarioResponseDto usuarioResponseDtoMock = new UsuarioResponseDto();
        
        when(usuarioRepository.existsByCorreo(usuarioRequestDto.getCorreo())).thenReturn(false);
        when(modelMapper.map(usuarioRequestDto, Usuario.class)).thenReturn(usuarioMock);
        when(modelMapper.map(usuarioMock, UsuarioResponseDto.class)).thenReturn(usuarioResponseDtoMock);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        try (MockedStatic<JwtUtil> mockedJwt = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwt.when(() -> JwtUtil.generarToken(usuarioMock.getUsuarioId()))
                     .thenReturn("mockedToken");
    
            UsuarioResponseDto responseDto = usuarioService.crearUsuario(usuarioRequestDto);
    
            assertNotNull(responseDto);
            assertNotNull(responseDto.getToken());
            assertEquals("mockedToken", responseDto.getToken());
    
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }
    }

    @Test
    public void testCrearUsuarioCorreoYaRegistrado() {

        UsuarioRequestDto request = new UsuarioRequestDto();
        request.setNombre("Juan Pérez");
        request.setCorreo("juan.perez@ejemplo.com");
        request.setContraseña("Contraseña123");

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        assertThrows(CorreoYaRegistradoException.class, () -> {
            usuarioService.crearUsuario(request);
        });
    }

    @Test
    public void testActualizarUsuario() {
        UUID usuarioId = UUID.randomUUID();
        UsuarioRequestDto requestDto = new UsuarioRequestDto();
        requestDto.setNombre("Juan Pérez Actualizado");
        requestDto.setCorreo("juan.perez@nuevoemail.com");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsuarioId(usuarioId);
        usuarioMock.setCorreo("juan.perez@ejemplo.com");
        usuarioMock.setNombre("Juan Pérez");

        UsuarioResponseDto usuarioResponseDtoMock = new UsuarioResponseDto();

        Telefono telefono = new Telefono();
        telefono.setNumero("123456789");
        telefono.setCodigoCiudad("1");
        telefono.setCodigoPais("56");
        usuarioMock.setTelefonos(new ArrayList<>());
        usuarioMock.getTelefonos().add(telefono);

        when(modelMapper.map(usuarioMock, UsuarioResponseDto.class)).thenReturn(usuarioResponseDtoMock);
        when(usuarioRepository.findByUsuarioId(usuarioId)).thenReturn(usuarioMock);
        when(usuarioRepository.existsByCorreo(requestDto.getCorreo())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        UsuarioResponseDto responseDto = usuarioService.actualizarUsuario(usuarioId, requestDto);

        assertNotNull(responseDto);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testEliminarUsuario() {
        UUID usuarioId = UUID.randomUUID();

        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);

        boolean result = usuarioService.eliminarUsuario(usuarioId);

        assertTrue(result);
        verify(usuarioRepository, times(1)).deleteById(usuarioId);
    }

    @Test
    public void testEliminarUsuarioNoEncontrado() {
        UUID usuarioId = UUID.randomUUID();

        when(usuarioRepository.existsById(usuarioId)).thenReturn(false);

        assertThrows(UsuarioNoEncontradoException.class, () -> {
            usuarioService.eliminarUsuario(usuarioId);
        });
    }
}
