package com.example.gestionecogarzones.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EstadoEvento {
    PENDIENTE, CONFIRMADO, EN_PROCESO, FINALIZADO, CANCELADO
}

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreCliente: String,
    val telefono: String,
    val tipoServicio: String,
    val tipoEvento: String,
    val fechaEvento: String,
    val direccion: String,
    val cantidadInvitados: Int,
    val restriccionesAlimentarias: String,
    val garzonesSolicitados: String,
    val cocinerosSolicitados: String,
    val bartendersSolicitados: String,
    val coperosSolicitados: String,
    val menusSolicitados: String,
    val barrasSolicitadas: String,
    val detalleInsumos: String,
    val aceptaTerminos: Boolean,
    val estado: EstadoEvento = EstadoEvento.PENDIENTE,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val creadoEn: Long = System.currentTimeMillis()
)