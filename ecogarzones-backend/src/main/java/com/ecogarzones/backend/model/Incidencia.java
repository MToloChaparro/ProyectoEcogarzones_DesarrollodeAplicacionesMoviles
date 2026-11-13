package com.ecogarzones.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "incidencias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Incidencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long solicitudId;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(length = 2000)
    private String descripcion;

    private String reportadoPor = "Administrador";

    @Enumerated(EnumType.STRING)
    private EstadoIncidencia estado = EstadoIncidencia.REPORTADA;

    @Column(length = 1000)
    private String fotoUri;
    private Long fecha;
}
