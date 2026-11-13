package com.example.gestionecogarzones.data.remote.dto

data class UsuarioDto(
    val id: Long? = null,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String,
    val fotoUri: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val cargoEspecialidad: String? = null
)