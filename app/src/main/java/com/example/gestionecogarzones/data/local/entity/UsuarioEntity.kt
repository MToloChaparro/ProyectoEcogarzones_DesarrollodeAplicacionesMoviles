package com.example.gestionecogarzones.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RolUsuario {
    CLIENTE,
    TRABAJADOR,
    ADMINISTRADOR
}

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: RolUsuario,
    val fotoUri: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val cargoEspecialidad: String? = null
)