package com.example.gestionecogarzones.data.repository

import com.example.gestionecogarzones.data.local.dao.IncidenciaDao
import com.example.gestionecogarzones.data.local.dao.InventarioDao
import com.example.gestionecogarzones.data.local.dao.SolicitudDao
import com.example.gestionecogarzones.data.local.dao.UsuarioDao
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.RolUsuario
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import com.example.gestionecogarzones.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(
    private val solicitudDao: SolicitudDao,
    private val incidenciaDao: IncidenciaDao,
    private val inventarioDao: InventarioDao,
    private val usuarioDao: UsuarioDao
) {
    var emailUsuarioActual: String = "matias@adminecogarzones.com"

    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.obtenerTodas()
    val todasLasIncidencias: Flow<List<IncidenciaEntity>> = incidenciaDao.obtenerTodas()
    val todoElInventario: Flow<List<InventarioEntity>> = inventarioDao.obtenerTodo()
    val todosLosTrabajadores: Flow<List<UsuarioEntity>> = usuarioDao.obtenerPorRolFlow(RolUsuario.TRABAJADOR)

    fun obtenerSolicitudPorId(id: Long): Flow<SolicitudEntity?> = solicitudDao.obtenerPorId(id)
    suspend fun crearSolicitud(solicitud: SolicitudEntity): Long = solicitudDao.insertar(solicitud)
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity) = solicitudDao.actualizar(solicitud)

    // --- FEEDBACK / ASIGNACIÓN / CHECKLIST ---
    suspend fun guardarFeedback(solicitud: SolicitudEntity, estrellas: Int, comentario: String) {
        solicitudDao.actualizar(solicitud.copy(calificacionEstrellas = estrellas, comentarioFeedback = comentario))
    }

    suspend fun asignarPersonalAEvento(solicitud: SolicitudEntity, personalAsignado: String) {
        solicitudDao.actualizar(solicitud.copy(garzonesAsignados = personalAsignado))
    }

    suspend fun guardarChecklistInsumos(solicitud: SolicitudEntity, checklistData: String) {
        solicitudDao.actualizar(solicitud.copy(checklistInsumos = checklistData))
    }

    fun obtenerIncidencias(solicitudId: Long): Flow<List<IncidenciaEntity>> = incidenciaDao.obtenerPorSolicitud(solicitudId)
    suspend fun registrarIncidencia(incidencia: IncidenciaEntity) = incidenciaDao.insertar(incidencia)
    suspend fun actualizarIncidencia(incidencia: IncidenciaEntity) = incidenciaDao.actualizar(incidencia)

    suspend fun actualizarInventario(item: InventarioEntity) = inventarioDao.actualizar(item)
    suspend fun contarInventario() = inventarioDao.contarItems()
    suspend fun agregarInventario(item: InventarioEntity) = inventarioDao.insertar(item)

    // --- MÉTODOS DE USUARIO ---
    suspend fun autenticarUsuario(email: String, pass: String): UsuarioEntity? = usuarioDao.autenticar(email, pass)
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity? = usuarioDao.obtenerPorEmail(email)
    fun obtenerUsuarioFlow(email: String): Flow<UsuarioEntity?> = usuarioDao.obtenerPorEmailFlow(email)
    suspend fun registrarUsuario(usuario: UsuarioEntity): Long = usuarioDao.insertar(usuario)
    suspend fun actualizarUsuario(usuario: UsuarioEntity) = usuarioDao.actualizar(usuario)

    suspend fun inicializarUsuariosPrueba() {
        if (usuarioDao.contarUsuarios() == 0) {
            val usuariosPrueba = listOf(
                UsuarioEntity(
                    nombre = "Matias",
                    email = "matias@adminecogarzones.com",
                    password = "matias123",
                    rol = RolUsuario.ADMINISTRADOR,
                    telefono = "+56 9 1234 5678",
                    direccion = "Av. Providencia 1234, Santiago",
                    cargoEspecialidad = "Director General"
                ),
                UsuarioEntity(
                    nombre = "Marcela",
                    email = "marcela@gmail.com",
                    password = "marcela123",
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 8765 4321",
                    direccion = "Av. Las Condes 5678, Santiago",
                    cargoEspecialidad = "Cliente VIP"
                ),
                UsuarioEntity(
                    nombre = "Alan",
                    email = "alan@personalecogarzones.com",
                    password = "alan123",
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 1111",
                    direccion = "Calle San Martín 88, Santiago",
                    cargoEspecialidad = "Garzón Principal"
                ),
                UsuarioEntity(
                    nombre = "Gabriela",
                    email = "gabriela@personalecogarzones.com",
                    password = "gabriela123",
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 2222",
                    direccion = "Av. Italia 432, Providencia",
                    cargoEspecialidad = "Bartender Senior"
                ),
                UsuarioEntity(
                    nombre = "Pim",
                    email = "pim@personalecogarzones.com",
                    password = "pim123",
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 3333",
                    direccion = "Calle O'Higgins 99, Santiago",
                    cargoEspecialidad = "Bartender Coctelería"
                )
            )
            usuarioDao.insertarTodos(usuariosPrueba)
        }
    }

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