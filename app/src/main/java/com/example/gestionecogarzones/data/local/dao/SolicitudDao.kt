package com.example.gestionecogarzones.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY creadoEn DESC")
    fun obtenerTodas(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE emailCliente = :email ORDER BY creadoEn DESC")
    fun obtenerPorEmailCliente(email: String): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<SolicitudEntity?>

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    @Insert
    suspend fun insertarTodas(solicitudes: List<SolicitudEntity>)

    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)

    @Query("SELECT COUNT(*) FROM solicitudes")
    suspend fun contarSolicitudes(): Int
}