package com.ecogarzones.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "solicitudes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Solicitud {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @Pattern(regexp = "\\d{8,}", message = "Teléfono inválido (mínimo 8 dígitos)")
    private String telefono;

    private String tipoServicio = "Banquetería";
    private String tipoEvento;

    @NotBlank(message = "La fecha del evento es obligatoria")
    private String fechaEvento;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Min(value = 1, message = "La cantidad de invitados debe ser mayor a 0")
    private int cantidadInvitados;

    @Column(length = 1000) private String restriccionesAlimentarias;
    private String garzonesSolicitados;
    private String cocinerosSolicitados;
    private String bartendersSolicitados;
    private String coperosSolicitados;
    @Column(length = 1000) private String menusSolicitados;
    @Column(length = 1000) private String barrasSolicitadas;
    @Column(length = 1000) private String detalleInsumos;
    private boolean aceptaTerminos;

    @Enumerated(EnumType.STRING)
    private EstadoEvento estado = EstadoEvento.PENDIENTE;

    private Double latitud;
    private Double longitud;
    private Integer calificacionEstrellas;
    @Column(length = 1000) private String comentarioFeedback;
    @Column(length = 1000) private String garzonesAsignados;
    @Column(length = 1000) private String checklistInsumos;
    private Long creadoEn;
}
