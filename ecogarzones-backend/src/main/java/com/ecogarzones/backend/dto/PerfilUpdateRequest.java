package com.ecogarzones.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record PerfilUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        String telefono,
        String direccion,
        String cargoEspecialidad,
        String fotoUri) {
}
