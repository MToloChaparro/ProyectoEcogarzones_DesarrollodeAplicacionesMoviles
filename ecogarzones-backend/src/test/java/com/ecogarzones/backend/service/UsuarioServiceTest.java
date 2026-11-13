package com.ecogarzones.backend.service;

import com.ecogarzones.backend.dto.LoginRequest;
import com.ecogarzones.backend.dto.RegistroRequest;
import com.ecogarzones.backend.dto.UsuarioResponse;
import com.ecogarzones.backend.exception.ApiException;
import com.ecogarzones.backend.model.Rol;
import com.ecogarzones.backend.model.Usuario;
import com.ecogarzones.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository repo;
    final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    UsuarioService service;

    @BeforeEach
    void setUp() { service = new UsuarioService(repo, encoder); }

    @Test
    void rolPorEmail_segunDominio() {
        assertEquals(Rol.ADMINISTRADOR, UsuarioService.rolPorEmail("x@adminecogarzones.com"));
        assertEquals(Rol.TRABAJADOR, UsuarioService.rolPorEmail("Y@PersonalEcogarzones.com"));
        assertEquals(Rol.CLIENTE, UsuarioService.rolPorEmail("z@gmail.com"));
    }

    @Test
    void registrar_hasheaPasswordYAsignaRol() {
        when(repo.existsByEmail("ana@personalecogarzones.com")).thenReturn(false);
        when(repo.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponse res = service.registrar(new RegistroRequest("Ana", "Ana@PersonalEcogarzones.com", "clave1", null, null, null, null));

        ArgumentCaptor<Usuario> cap = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(cap.capture());
        assertEquals("TRABAJADOR", res.rol());
        assertNotEquals("clave1", cap.getValue().getPasswordHash());
        assertTrue(encoder.matches("clave1", cap.getValue().getPasswordHash()));
    }

    @Test
    void registrar_emailDuplicado_lanzaConflict() {
        when(repo.existsByEmail("dup@gmail.com")).thenReturn(true);
        ApiException ex = assertThrows(ApiException.class,
                () -> service.registrar(new RegistroRequest("Dup", "dup@gmail.com", "1234", null, null, null, null)));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(repo, never()).save(any());
    }

    @Test
    void autenticar_credencialesCorrectas() {
        Usuario u = new Usuario(1L, "Marcela", "marcela@gmail.com", encoder.encode("marcela123"), Rol.CLIENTE, null, null, null, null);
        when(repo.findByEmail("marcela@gmail.com")).thenReturn(Optional.of(u));
        assertEquals("CLIENTE", service.autenticar(new LoginRequest("marcela@gmail.com", "marcela123")).rol());
    }

    @Test
    void autenticar_passwordIncorrecta_lanzaUnauthorized() {
        Usuario u = new Usuario(1L, "Marcela", "marcela@gmail.com", encoder.encode("marcela123"), Rol.CLIENTE, null, null, null, null);
        when(repo.findByEmail("marcela@gmail.com")).thenReturn(Optional.of(u));
        ApiException ex = assertThrows(ApiException.class,
                () -> service.autenticar(new LoginRequest("marcela@gmail.com", "mala")));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }
}
