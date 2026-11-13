package com.ecogarzones.backend.service;

import com.ecogarzones.backend.dto.*;
import com.ecogarzones.backend.exception.ApiException;
import com.ecogarzones.backend.model.Rol;
import com.ecogarzones.backend.model.Usuario;
import com.ecogarzones.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    /** Mismo criterio que la app: el rol lo define el dominio del correo, nunca el cliente. */
    public static Rol rolPorEmail(String email) {
        String e = email.trim().toLowerCase();
        if (e.endsWith("@adminecogarzones.com")) return Rol.ADMINISTRADOR;
        if (e.endsWith("@personalecogarzones.com")) return Rol.TRABAJADOR;
        return Rol.CLIENTE;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest r) {
        String email = r.email().trim().toLowerCase();
        if (repo.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Este correo electrónico ya está registrado");
        }
        Usuario u = new Usuario();
        u.setNombre(r.nombre().trim());
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(r.password()));
        u.setRol(rolPorEmail(email));
        u.setTelefono(r.telefono());
        u.setDireccion(r.direccion());
        u.setCargoEspecialidad(r.cargoEspecialidad());
        u.setFotoUri(r.fotoUri());
        return UsuarioResponse.from(repo.save(u));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse autenticar(LoginRequest r) {
        Usuario u = repo.findByEmail(r.email().trim().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!encoder.matches(r.password(), u.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        return UsuarioResponse.from(u);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repo.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(String email, PerfilUpdateRequest r) {
        Usuario u = repo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        u.setNombre(r.nombre().trim());
        u.setTelefono(r.telefono());
        u.setDireccion(r.direccion());
        u.setCargoEspecialidad(r.cargoEspecialidad());
        if (r.fotoUri() != null) u.setFotoUri(r.fotoUri());
        return UsuarioResponse.from(repo.save(u));
    }
}
