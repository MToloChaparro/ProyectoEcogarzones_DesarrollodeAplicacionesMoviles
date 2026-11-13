package com.ecogarzones.backend.dto;

import com.ecogarzones.backend.model.Usuario;

/** Nunca expone la contraseña ni su hash. */
public record UsuarioResponse(Long id, String nombre, String email, String rol, String fotoUri,
                              String telefono, String direccion, String cargoEspecialidad) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol().name(),
                u.getFotoUri(), u.getTelefono(), u.getDireccion(), u.getCargoEspecialidad());
    }
}
