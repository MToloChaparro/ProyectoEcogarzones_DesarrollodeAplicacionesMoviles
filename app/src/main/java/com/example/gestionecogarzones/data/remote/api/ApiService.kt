package com.example.gestionecogarzones.data.remote.api

import com.example.gestionecogarzones.data.remote.dto.SolicitudDto
import com.example.gestionecogarzones.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): Response<List<UsuarioDto>>

    @POST("api/usuarios/login")
    suspend fun autenticarUsuario(@Body request: UsuarioDto): Response<UsuarioDto>

    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(@Body usuario: UsuarioDto): Response<UsuarioDto>

    @GET("api/solicitudes")
    suspend fun obtenerSolicitudes(): Response<List<SolicitudDto>>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    @GET("api/solicitudes/{id}")
    suspend fun obtenerSolicitudPorId(@Path("id") id: Long): Response<SolicitudDto>
}