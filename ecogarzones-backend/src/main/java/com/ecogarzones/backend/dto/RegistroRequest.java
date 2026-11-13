package com.ecogarzones.backend.dto;

import jakarta.validation.constraints.*;

public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank @Email(message = "Correo inválido") String email,
        @NotBlank @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres") String password,
        String telefono,
        String direccion,
        String cargoEspecialidad,
        String fotoUri) {
}
