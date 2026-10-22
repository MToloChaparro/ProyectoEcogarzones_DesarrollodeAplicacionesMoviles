package com.example.gestionecogarzones.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidenciaDao {
    @Query("SELECT * FROM incidencias ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<IncidenciaEntity>>

    @Query("SELECT * FROM incidencias WHERE solicitudId = :solicitudId ORDER BY fecha DESC")
    fun obtenerPorSolicitud(solicitudId: Long): Flow<List<IncidenciaEntity>>

    @Insert
    suspend fun insertar(incidencia: IncidenciaEntity)

    @Update
    suspend fun actualizar(incidencia: IncidenciaEntity)
}