package com.example.gestionecogarzones.data.repository

import com.example.gestionecogarzones.data.local.dao.IncidenciaDao
import com.example.gestionecogarzones.data.local.dao.InventarioDao
import com.example.gestionecogarzones.data.local.dao.SolicitudDao
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(
    private val solicitudDao: SolicitudDao,
    private val incidenciaDao: IncidenciaDao,
    private val inventarioDao: InventarioDao
) {
    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.obtenerTodas()
    val todasLasIncidencias: Flow<List<IncidenciaEntity>> = incidenciaDao.obtenerTodas()
    val todoElInventario: Flow<List<InventarioEntity>> = inventarioDao.obtenerTodo()

    fun obtenerSolicitudPorId(id: Long): Flow<SolicitudEntity?> = solicitudDao.obtenerPorId(id)
    suspend fun crearSolicitud(solicitud: SolicitudEntity): Long = solicitudDao.insertar(solicitud)
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity) = solicitudDao.actualizar(solicitud)

    fun obtenerIncidencias(solicitudId: Long): Flow<List<IncidenciaEntity>> = incidenciaDao.obtenerPorSolicitud(solicitudId)
    suspend fun registrarIncidencia(incidencia: IncidenciaEntity) = incidenciaDao.insertar(incidencia)
    suspend fun actualizarIncidencia(incidencia: IncidenciaEntity) = incidenciaDao.actualizar(incidencia)

    suspend fun actualizarInventario(item: InventarioEntity) = inventarioDao.actualizar(item)
    suspend fun contarInventario() = inventarioDao.contarItems()
    suspend fun agregarInventario(item: InventarioEntity) = inventarioDao.insertar(item)

    suspend fun inicializarInventario() {
        val itemsIniciales = listOf(
            InventarioEntity(nombreItem = "Platos principales", cantidad = 150, categoria = "Vajilla", unidad = "Unidades", costoUnidad = 1500.0, proveedor = "Local"),
            InventarioEntity(nombreItem = "Copas de vino", cantidad = 200, categoria = "Cristalería", unidad = "Unidades", costoUnidad = 1000.0, proveedor = "Importadora X"),
            InventarioEntity(nombreItem = "Sillas Tiffany", cantidad = 100, categoria = "Mobiliario", unidad = "Unidades", costoUnidad = 15000.0, proveedor = "Arriendos SPA"),
            InventarioEntity(nombreItem = "Manteles blancos", cantidad = 30, categoria = "Textiles", unidad = "Paquetes", costoUnidad = 5000.0, proveedor = "Textiles Sur")
        )
        inventarioDao.insertarTodo(itemsIniciales)
    }
}