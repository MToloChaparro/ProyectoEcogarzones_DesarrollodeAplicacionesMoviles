package com.example.gestionecogarzones.data.remote.api

import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import retrofit2.http.*

interface ApiService {
    @GET("api/solicitudes")
    suspend fun getSolicitudes(): List<SolicitudEntity>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudEntity): SolicitudEntity

    @GET("api/inventario")
    suspend fun getInventario(): List<InventarioEntity>

    @GET("api/incidencias")
    suspend fun getIncidencias(): List<IncidenciaEntity>
}