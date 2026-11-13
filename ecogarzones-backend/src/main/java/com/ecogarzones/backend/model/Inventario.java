package com.ecogarzones.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "inventario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Inventario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del ítem es obligatorio")
    private String nombreItem;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private int cantidad;

    private String categoria;
    private String unidad;
    private String detalle = "";
    private String estado = "Disponible";
    private String fechaAdquisicion = "";

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private double costoUnidad;

    private String proveedor = "";
}
