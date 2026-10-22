package com.example.gestionecogarzones.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventario")
data class InventarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreItem: String,
    val cantidad: Int,
    val categoria: String,
    val unidad: String,
    val detalle: String = "",
    val estado: String = "Disponible",
    val fechaAdquisicion: String = "",
    val costoUnidad: Double = 0.0,
    val proveedor: String = ""
) {
    val valorTotal: Double
        get() = cantidad * costoUnidad
}