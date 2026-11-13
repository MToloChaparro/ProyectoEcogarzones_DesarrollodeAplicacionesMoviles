package com.example.gestionecogarzones.data.remote.dto

data class SolicitudDto(
    val id: Long? = null,
    val nombreCliente: String,
    val telefono: String,
    val tipoEvento: String,
    val fechaEvento: String,
    val direccion: String,
    val cantidadInvitados: Int,
    val estado: String = "PENDIENTE"
)