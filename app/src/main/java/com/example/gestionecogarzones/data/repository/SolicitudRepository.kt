package com.example.gestionecogarzones.data.repository

import com.example.gestionecogarzones.data.local.dao.IncidenciaDao
import com.example.gestionecogarzones.data.local.dao.InventarioDao
import com.example.gestionecogarzones.data.local.dao.SolicitudDao
import com.example.gestionecogarzones.data.local.dao.UsuarioDao
import com.example.gestionecogarzones.data.local.entity.EstadoEvento
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.RolUsuario
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import com.example.gestionecogarzones.data.local.entity.UsuarioEntity
import com.example.gestionecogarzones.utils.PasswordHasher
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(
    private val solicitudDao: SolicitudDao,
    private val incidenciaDao: IncidenciaDao,
    private val inventarioDao: InventarioDao,
    private val usuarioDao: UsuarioDao
) {
    var emailUsuarioActual: String = ""

    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.obtenerTodas()
    val todasLasIncidencias: Flow<List<IncidenciaEntity>> = incidenciaDao.obtenerTodas()
    val todoElInventario: Flow<List<InventarioEntity>> = inventarioDao.obtenerTodo()
    val todosLosTrabajadores: Flow<List<UsuarioEntity>> = usuarioDao.obtenerPorRolFlow(RolUsuario.TRABAJADOR)
    val todosLosUsuarios: Flow<List<UsuarioEntity>> = usuarioDao.obtenerTodos()

    fun obtenerSolicitudesPorClienteFlow(email: String): Flow<List<SolicitudEntity>> =
        solicitudDao.obtenerPorEmailCliente(email)

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
    suspend fun autenticarUsuario(email: String, pass: String): UsuarioEntity? {
        val hashedPass = PasswordHasher.hashPassword(pass)
        var usuario = usuarioDao.autenticar(email, hashedPass)
        if (usuario == null) {
            val usuarioLegacy = usuarioDao.autenticar(email, pass)
            if (usuarioLegacy != null) {
                val actualizado = usuarioLegacy.copy(password = hashedPass)
                usuarioDao.actualizar(actualizado)
                usuario = actualizado
            }
        }
        return usuario
    }

    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity? = usuarioDao.obtenerPorEmail(email)
    fun obtenerUsuarioFlow(email: String): Flow<UsuarioEntity?> = usuarioDao.obtenerPorEmailFlow(email)
    suspend fun registrarUsuario(usuario: UsuarioEntity): Long {
        val usuarioConHash = usuario.copy(password = PasswordHasher.hashPassword(usuario.password))
        return usuarioDao.insertar(usuarioConHash)
    }
    suspend fun actualizarUsuario(usuario: UsuarioEntity) = usuarioDao.actualizar(usuario)

    suspend fun inicializarUsuariosPrueba() {
        if (usuarioDao.contarUsuarios() == 0) {
            val usuariosPrueba = listOf(
                // Administradores
                UsuarioEntity(
                    nombre = "Matías Admin",
                    email = "matias@adminecogarzones.com",
                    password = PasswordHasher.hashPassword("matias123"),
                    rol = RolUsuario.ADMINISTRADOR,
                    telefono = "+56 9 1234 5678",
                    direccion = "Av. Providencia 1234, Santiago",
                    cargoEspecialidad = "Director General"
                ),
                UsuarioEntity(
                    nombre = "Sofía Admin",
                    email = "sofia@adminecogarzones.com",
                    password = PasswordHasher.hashPassword("sofia123"),
                    rol = RolUsuario.ADMINISTRADOR,
                    telefono = "+56 9 2233 4455",
                    direccion = "Av. Apoquindo 4500, Las Condes",
                    cargoEspecialidad = "Jefa de Logística"
                ),

                // Clientes
                UsuarioEntity(
                    nombre = "Marcela Valenzuela",
                    email = "marcela@gmail.com",
                    password = PasswordHasher.hashPassword("marcela123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 8765 4321",
                    direccion = "Av. Las Condes 5678, Santiago",
                    cargoEspecialidad = "Cliente VIP"
                ),
                UsuarioEntity(
                    nombre = "Carlos Pérez",
                    email = "carlos.perez@gmail.com",
                    password = PasswordHasher.hashPassword("carlos123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 3344 5566",
                    direccion = "Calle San Francisco 120, Santiago Centro",
                    cargoEspecialidad = "Cliente Corporativo"
                ),
                UsuarioEntity(
                    nombre = "Valentina Silva",
                    email = "valentina.silva@outlook.com",
                    password = PasswordHasher.hashPassword("valentina123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 4455 6677",
                    direccion = "Av. Vitacura 8900, Vitacura",
                    cargoEspecialidad = "Cliente Frecuente"
                ),
                UsuarioEntity(
                    nombre = "Esteban Mora",
                    email = "esteban.mora@yahoo.com",
                    password = PasswordHasher.hashPassword("esteban123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 5566 7788",
                    direccion = "Calle Carmen 550, Santiago",
                    cargoEspecialidad = "Cliente Particular"
                ),
                UsuarioEntity(
                    nombre = "Loreto Araya",
                    email = "loreto.araya@gmail.com",
                    password = PasswordHasher.hashPassword("loreto123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 6677 8899",
                    direccion = "Av. Bilbao 2340, Providencia",
                    cargoEspecialidad = "Cliente Matrimonios"
                ),
                UsuarioEntity(
                    nombre = "Gonzalo Fuentes",
                    email = "gonzalo.fuentes@empresa.cl",
                    password = PasswordHasher.hashPassword("gonzalo123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 7788 9900",
                    direccion = "Calle Estado 150, Santiago Centro",
                    cargoEspecialidad = "Cliente Empresa"
                ),
                UsuarioEntity(
                    nombre = "Beatriz Larraín",
                    email = "beatriz.larrain@uc.cl",
                    password = PasswordHasher.hashPassword("beatriz123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 8899 0011",
                    direccion = "Av. San Carlos de Apoquindo 2200, Las Condes",
                    cargoEspecialidad = "Cliente Graduaciones"
                ),
                UsuarioEntity(
                    nombre = "Felipe Ossa",
                    email = "felipe.ossa@gmail.com",
                    password = PasswordHasher.hashPassword("felipe123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 9900 1122",
                    direccion = "Calle Suecia 850, Providencia",
                    cargoEspecialidad = "Cliente Aniversarios"
                ),
                UsuarioEntity(
                    nombre = "Camila Jorquera",
                    email = "camila.jorquera@hoteles.cl",
                    password = PasswordHasher.hashPassword("camila123"),
                    rol = RolUsuario.CLIENTE,
                    telefono = "+56 9 1122 3344",
                    direccion = "Av. Alonso de Córdova 3800, Vitacura",
                    cargoEspecialidad = "Cliente Banquetería Hotelera"
                ),

                // Trabajadores / Personal
                UsuarioEntity(
                    nombre = "Alan González",
                    email = "alan@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("alan123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 1111",
                    direccion = "Calle San Martín 88, Santiago",
                    cargoEspecialidad = "Garzón Principal"
                ),
                UsuarioEntity(
                    nombre = "Gabriela Soto",
                    email = "gabriela@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("gabriela123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 2222",
                    direccion = "Av. Italia 432, Providencia",
                    cargoEspecialidad = "Bartender Senior"
                ),
                UsuarioEntity(
                    nombre = "Pim Rojas",
                    email = "pim@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("pim123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 3333",
                    direccion = "Calle O'Higgins 99, Santiago",
                    cargoEspecialidad = "Bartender Coctelería"
                ),
                UsuarioEntity(
                    nombre = "Roberto Díaz",
                    email = "roberto@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("roberto123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 4444",
                    direccion = "Av. Grecia 1200, Ñuñoa",
                    cargoEspecialidad = "Cocinero Chef"
                ),
                UsuarioEntity(
                    nombre = "Camila Morales",
                    email = "camila@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("camila123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 5555",
                    direccion = "Calle Manuel Montt 780, Providencia",
                    cargoEspecialidad = "Copero y Logística"
                ),
                UsuarioEntity(
                    nombre = "Ignacio Tapia",
                    email = "ignacio@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("ignacio123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 6666",
                    direccion = "Av. Matta 450, Santiago",
                    cargoEspecialidad = "Capitán de Garzones"
                ),
                UsuarioEntity(
                    nombre = "Daniela Muñoz",
                    email = "daniela@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("daniela123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 7777",
                    direccion = "Calle Lastarria 110, Santiago",
                    cargoEspecialidad = "Sommelier & Vinos"
                ),
                UsuarioEntity(
                    nombre = "Javier Castro",
                    email = "javier@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("javier123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 8888",
                    direccion = "Av. Macul 2800, Macul",
                    cargoEspecialidad = "Parrillero Experto"
                ),
                UsuarioEntity(
                    nombre = "Fernanda Palma",
                    email = "fernanda@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("fernanda123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 9999",
                    direccion = "Calle Tobalaba 1500, Providencia",
                    cargoEspecialidad = "Garzona VIP"
                ),
                UsuarioEntity(
                    nombre = "Matías Vega",
                    email = "mvega@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("mvega123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 0000",
                    direccion = "Calle Independencia 620, Independencia",
                    cargoEspecialidad = "Ayudante de Cocina & Montaje"
                ),
                UsuarioEntity(
                    nombre = "Patricia Henríquez",
                    email = "patricia@personalecogarzones.com",
                    password = PasswordHasher.hashPassword("patricia123"),
                    rol = RolUsuario.TRABAJADOR,
                    telefono = "+56 9 5555 1234",
                    direccion = "Av. Larraín 6600, La Reina",
                    cargoEspecialidad = "Barwoman Coctelería Autor"
                )
            )
            usuarioDao.insertarTodos(usuariosPrueba)
        }
    }

    suspend fun inicializarSolicitudesPrueba() {
        if (solicitudDao.contarSolicitudes() == 0) {
            val solicitudesPrueba = listOf(
                SolicitudEntity(
                    emailCliente = "marcela@gmail.com",
                    nombreCliente = "Marcela Valenzuela",
                    telefono = "+56 9 8765 4321",
                    tipoServicio = "Banquetería Completa",
                    tipoEvento = "Boda Elegante",
                    fechaEvento = "25/11/2026",
                    direccion = "Av. Las Condes 5678, Las Condes",
                    cantidadInvitados = 80,
                    restriccionesAlimentarias = "3 celíacos, 5 vegetarianos",
                    garzonesSolicitados = "6",
                    cocinerosSolicitados = "2",
                    bartendersSolicitados = "2",
                    coperosSolicitados = "1",
                    menusSolicitados = "50 Tradicional - Carne, 25 Tradicional - Pollo, 5 Celiaco (Sin Gluten)",
                    barrasSolicitadas = "80 Coctelería",
                    detalleInsumos = "Ecogarzones provee todo",
                    aceptaTerminos = true,
                    estado = EstadoEvento.CONFIRMADO,
                    garzonesAsignados = "Alan González, Gabriela Soto, Ignacio Tapia"
                ),
                SolicitudEntity(
                    emailCliente = "carlos.perez@gmail.com",
                    nombreCliente = "Carlos Pérez",
                    telefono = "+56 9 3344 5566",
                    tipoServicio = "Cóctel / Coffee Break",
                    tipoEvento = "Seminario Empresarial",
                    fechaEvento = "10/12/2026",
                    direccion = "Av. Andrés Bello 2777, Providencia",
                    cantidadInvitados = 45,
                    restriccionesAlimentarias = "2 veganos",
                    garzonesSolicitados = "3",
                    cocinerosSolicitados = "0",
                    bartendersSolicitados = "1",
                    coperosSolicitados = "0",
                    menusSolicitados = "45 Coffee Break Ejecutivo",
                    barrasSolicitadas = "45 Jugos y Gaseosas",
                    detalleInsumos = "Yo llevo solo vajilla",
                    aceptaTerminos = true,
                    estado = EstadoEvento.EN_PROCESO,
                    garzonesAsignados = "Pim Rojas, Roberto Díaz, Daniela Muñoz"
                ),
                SolicitudEntity(
                    emailCliente = "valentina.silva@outlook.com",
                    nombreCliente = "Valentina Silva",
                    telefono = "+56 9 4455 6677",
                    tipoServicio = "Solo Barra",
                    tipoEvento = "Fiesta de Graduación",
                    fechaEvento = "05/12/2026",
                    direccion = "Av. Vitacura 8900, Vitacura",
                    cantidadInvitados = 120,
                    restriccionesAlimentarias = "Ninguna",
                    garzonesSolicitados = "0",
                    cocinerosSolicitados = "0",
                    bartendersSolicitados = "4",
                    coperosSolicitados = "3",
                    menusSolicitados = "Sin menús",
                    barrasSolicitadas = "120 Coctelería",
                    detalleInsumos = "Ecogarzones provee todo",
                    aceptaTerminos = true,
                    estado = EstadoEvento.PENDIENTE
                ),
                SolicitudEntity(
                    emailCliente = "esteban.mora@yahoo.com",
                    nombreCliente = "Esteban Mora",
                    telefono = "+56 9 5566 7788",
                    tipoServicio = "Banquetería Completa",
                    tipoEvento = "Aniversario Corporativo",
                    fechaEvento = "15/10/2026",
                    direccion = "Calle Carmen 550, Santiago",
                    cantidadInvitados = 60,
                    restriccionesAlimentarias = "4 vegetarianos",
                    garzonesSolicitados = "6",
                    cocinerosSolicitados = "2",
                    bartendersSolicitados = "2",
                    coperosSolicitados = "1",
                    menusSolicitados = "56 Tradicional - Pescado, 4 Vegetariano",
                    barrasSolicitadas = "60 Coctelería",
                    detalleInsumos = "Ecogarzones provee todo",
                    aceptaTerminos = true,
                    estado = EstadoEvento.FINALIZADO,
                    garzonesAsignados = "Camila Morales, Alan González, Fernanda Palma",
                    calificacionEstrellas = 5,
                    comentarioFeedback = "Excelente servicio y atención de los garzones."
                ),
                SolicitudEntity(
                    emailCliente = "loreto.araya@gmail.com",
                    nombreCliente = "Loreto Araya",
                    telefono = "+56 9 6677 8899",
                    tipoServicio = "Banquetería Completa",
                    tipoEvento = "Matrimonio Civil",
                    fechaEvento = "18/12/2026",
                    direccion = "Av. Bilbao 2340, Providencia",
                    cantidadInvitados = 50,
                    restriccionesAlimentarias = "2 veganos, 1 celiaco",
                    garzonesSolicitados = "4",
                    cocinerosSolicitados = "1",
                    bartendersSolicitados = "1",
                    coperosSolicitados = "1",
                    menusSolicitados = "40 Tradicional - Carne, 8 Vegano, 2 Celiaco (Sin Gluten)",
                    barrasSolicitadas = "50 Coctelería",
                    detalleInsumos = "Ecogarzones provee todo",
                    aceptaTerminos = true,
                    estado = EstadoEvento.CONFIRMADO,
                    garzonesAsignados = "Fernanda Palma, Javier Castro"
                ),
                SolicitudEntity(
                    emailCliente = "gonzalo.fuentes@empresa.cl",
                    nombreCliente = "Gonzalo Fuentes",
                    telefono = "+56 9 7788 9900",
                    tipoServicio = "Cóctel / Coffee Break",
                    tipoEvento = "Lanzamiento Producto Tech",
                    fechaEvento = "20/12/2026",
                    direccion = "Calle Estado 150, Santiago Centro",
                    cantidadInvitados = 90,
                    restriccionesAlimentarias = "Sin gluten",
                    garzonesSolicitados = "5",
                    cocinerosSolicitados = "0",
                    bartendersSolicitados = "2",
                    coperosSolicitados = "1",
                    menusSolicitados = "90 Canapés Salados",
                    barrasSolicitadas = "90 Coctelería",
                    detalleInsumos = "Ecogarzones provee todo",
                    aceptaTerminos = true,
                    estado = EstadoEvento.PENDIENTE
                )
            )
            solicitudDao.insertarTodas(solicitudesPrueba)
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
