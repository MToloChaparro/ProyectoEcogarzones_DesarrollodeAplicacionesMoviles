package com.example.gestionecogarzones.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EstadoIncidencia {
    REPORTADA,
    EN_PROCESO,
    RESUELTA
}

@Entity(tableName = "incidencias")
data class IncidenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val solicitudId: Long,
    val tipo: String,
    val descripcion: String,
    val reportadoPor: String = "Administrador",
    val estado: EstadoIncidencia = EstadoIncidencia.REPORTADA,
    val fotoUri: String? = null,
    val fecha: Long = System.currentTimeMillis()
)