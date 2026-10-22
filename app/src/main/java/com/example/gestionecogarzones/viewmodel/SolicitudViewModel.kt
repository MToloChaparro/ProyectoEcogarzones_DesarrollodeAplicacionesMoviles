package com.example.gestionecogarzones.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolicitudViewModel(
    private val repository: SolicitudRepository
) : ViewModel() {

    private val catalogoMenusBanqueteria = mapOf(
        "Tradicional - Carne" to listOf("Entrada: Consomé", "Fondo: Lomo a lo pobre", "Postre: Leche asada"),
        "Tradicional - Pescado" to listOf("Entrada: Ceviche", "Fondo: Salmón grillado", "Postre: Mousse de maracuyá"),
        "Tradicional - Pollo" to listOf("Entrada: Ensalada César", "Fondo: Pollo relleno", "Postre: Tiramisú"),
        "Vegetariano" to listOf("Entrada: Bruschettas", "Fondo: Lasagna de verduras", "Postre: Frutas de estación"),
        "Vegano" to listOf("Entrada: Tartar de palta", "Fondo: Risotto de champiñones", "Postre: Helado de coco"),
        "Celiaco (Sin Gluten)" to listOf("Entrada: Carpaccio", "Fondo: Filete con puré", "Postre: Panna cotta sin gluten")
    )

    private val catalogoMenusCoctel = mapOf(
        "Canapés Salados" to listOf("Mini empanadas", "Brochetas de pollo", "Mini quiches", "Tapaditos de jamón", "Mini Hamburguesas", "Mini Sopaipillas"),
        "Canapés Dulces" to listOf("Mini brownies", "Tartaletas de fruta", "Mini Pie de Limón", "Mini cheesecakes de Maracuyá", "Mini cheesecakes de Frutos Rojos", "Alfajores de nuez"),
        "Coffee Break Ejecutivo" to listOf("Sándwiches de miga (Huevo)", "Sándwiches de miga (Avemayo)", "Medialunas", "Croissants","Mini Sopaipillas" , "Jugo + Café")
    )

    private val catalogoBarrasCompleta = mapOf(
        "Coctelería" to listOf("Pisco Sour", "Mojito", "Aperol Spritz"),
        "Mockteles (Sin Alcohol)" to listOf("Virgin Mojito", "Piña Colada sin alcohol", "Limonada suiza"),
        "Jugos y Gaseosas" to listOf("Jugo natural", "Bebidas gaseosas", "Agua mineral"),
        "Bebidas Calientes" to listOf("Café espresso", "Té en hebras", "Mate cocido")
    )

    private val catalogoBarrasCoctel = mapOf(
        "Jugos y Gaseosas" to listOf("Jugo natural", "Bebidas gaseosas", "Agua mineral"),
        "Bebidas Calientes" to listOf("Café espresso", "Té en hebras", "Mate cocido")
    )

    val catalogoMenusActual: Map<String, List<String>>
        get() = if (tipoServicio == "Cóctel / Coffee Break") catalogoMenusCoctel else catalogoMenusBanqueteria

    val catalogoBarrasActual: Map<String, List<String>>
        get() = if (tipoServicio == "Cóctel / Coffee Break") catalogoBarrasCoctel else catalogoBarrasCompleta

    var nombreCliente by mutableStateOf("")
        private set
    var telefono by mutableStateOf("")
        private set
    var tipoServicio by mutableStateOf("Banquetería Completa")
        private set
    var tipoEvento by mutableStateOf("")
        private set
    var fechaEvento by mutableStateOf("")
        private set
    var direccion by mutableStateOf("")
        private set
    var cantidadInvitados by mutableStateOf("")
        private set
    var restricciones by mutableStateOf("")
        private set
    var tipoInsumos by mutableStateOf("")
        private set

    var nombreError by mutableStateOf<String?>(null)
        private set
    var telefonoError by mutableStateOf<String?>(null)
        private set
    var fechaError by mutableStateOf<String?>(null)
        private set
    var direccionError by mutableStateOf<String?>(null)
        private set
    var invitadosError by mutableStateOf<String?>(null)
        private set

    var garzonesSugeridos by mutableStateOf(0)
        private set
    var cocinerosSugeridos by mutableStateOf(0)
        private set
    var bartendersSugeridos by mutableStateOf(0)
        private set
    var coperosSugeridos by mutableStateOf(0)
        private set

    var menusSeleccionados = mutableStateMapOf<String, Int>()
        private set
    var barrasSeleccionadas = mutableStateMapOf<String, Int>()
        private set

    var errorMenu by mutableStateOf<String?>(null)
        private set
    var errorBarra by mutableStateOf<String?>(null)
        private set

    var aceptaTerminos by mutableStateOf(false)
        private set
    var isGuardando by mutableStateOf(false)
        private set
    var mostrarResumen by mutableStateOf(false)
        private set

    val fechaMinima: Long = System.currentTimeMillis() + (15L * 24 * 60 * 60 * 1000)

    fun onNombreChange(value: String) {
        nombreCliente = value
        nombreError = if (value.length < 3) "El nombre debe tener al menos 3 letras" else null
    }

    fun onTelefonoChange(value: String) {
        telefono = value
        telefonoError = if (value.length < 8 || !value.all { it.isDigit() }) "Teléfono inválido" else null
    }

    fun onTipoEventoChange(value: String) { tipoEvento = value }
    fun onTipoInsumosChange(value: String) { tipoInsumos = value }

    fun onFechaSeleccionada(millis: Long) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        fechaEvento = sdf.format(Date(millis))
        fechaError = null
    }

    fun onDireccionChange(value: String) {
        direccion = value
        direccionError = if (value.isBlank()) "La dirección es obligatoria" else null
    }

    fun onRestriccionesChange(value: String) { restricciones = value }
    fun onTerminosChange(value: Boolean) { aceptaTerminos = value }

    fun onTipoServicioChange(value: String) {
        tipoServicio = value
        menusSeleccionados.clear()
        barrasSeleccionadas.clear()
        if (cantidadInvitados.isNotBlank() && invitadosError == null) {
            calcularPersonal()
        }
    }

    fun onInvitadosChange(value: String) {
        cantidadInvitados = value
        val numero = value.toIntOrNull()
        invitadosError = if (numero == null || numero <= 0) "Debe ser mayor a 0" else null

        if (invitadosError == null) {
            calcularPersonal()
        } else {
            resetearPersonal()
        }
    }

    private fun calcularPersonal() {
        val numero = cantidadInvitados.toIntOrNull() ?: 0
        if (numero <= 0) return

        when (tipoServicio) {
            "Cóctel / Coffee Break" -> {
                garzonesSugeridos = (numero + 19) / 20
                cocinerosSugeridos = 0
                bartendersSugeridos = if (numero > 30) (numero + 49) / 50 else 0
                coperosSugeridos = 0
            }
            "Solo Barra" -> {
                garzonesSugeridos = 0
                cocinerosSugeridos = 0
                bartendersSugeridos = (numero + 29) / 30
                coperosSugeridos = (numero + 49) / 50
            }
            else -> {
                val bloques = (numero + 39) / 40
                garzonesSugeridos = bloques * 3
                cocinerosSugeridos = bloques
                bartendersSugeridos = bloques
                coperosSugeridos = if (numero > 50) bloques else 1
            }
        }
        validarCantidades()
    }

    private fun resetearPersonal() {
        garzonesSugeridos = 0; cocinerosSugeridos = 0
        bartendersSugeridos = 0; coperosSugeridos = 0
    }

    fun actualizarCantidadMenu(tipo: String, cantidad: Int) {
        if (cantidad <= 0) menusSeleccionados.remove(tipo) else menusSeleccionados[tipo] = cantidad
        validarCantidades()
    }

    fun actualizarCantidadBarra(tipo: String, cantidad: Int) {
        if (cantidad <= 0) barrasSeleccionadas.remove(tipo) else barrasSeleccionadas[tipo] = cantidad
        validarCantidades()
    }

    private fun validarCantidades() {
        val totalInvitados = cantidadInvitados.toIntOrNull() ?: 0
        val totalMenus = menusSeleccionados.values.sum()
        val totalBarras = barrasSeleccionadas.values.sum()

        errorMenu = if (totalMenus > totalInvitados) "La suma de menús ($totalMenus) supera a los invitados ($totalInvitados)" else null
        errorBarra = if (totalBarras > totalInvitados) "La suma de barras ($totalBarras) supera a los invitados ($totalInvitados)" else null
    }

    fun irAResumen() {
        if (formularioEsValido() && aceptaTerminos) {
            mostrarResumen = true
        }
    }

    fun volverAEditar() { mostrarResumen = false }

    fun formularioEsValido(): Boolean {
        return nombreError == null && nombreCliente.isNotBlank() &&
                telefonoError == null && telefono.isNotBlank() &&
                fechaError == null && fechaEvento.isNotBlank() &&
                direccionError == null && direccion.isNotBlank() &&
                invitadosError == null && cantidadInvitados.isNotBlank() &&
                errorMenu == null && errorBarra == null
    }

    fun guardarSolicitud(onSuccess: () -> Unit) {
        if (!formularioEsValido() || !aceptaTerminos) return

        viewModelScope.launch {
            isGuardando = true
            try {
                val stringMenus = menusSeleccionados.entries.joinToString(", ") { "${it.value} ${it.key}" }
                val stringBarras = barrasSeleccionadas.entries.joinToString(", ") { "${it.value} ${it.key}" }

                val nuevaSolicitud = SolicitudEntity(
                    nombreCliente = nombreCliente,
                    telefono = telefono,
                    tipoServicio = tipoServicio,
                    tipoEvento = tipoEvento,
                    fechaEvento = fechaEvento,
                    direccion = direccion,
                    cantidadInvitados = cantidadInvitados.toIntOrNull() ?: 0,
                    restriccionesAlimentarias = restricciones,
                    garzonesSolicitados = garzonesSugeridos.toString(),
                    cocinerosSolicitados = cocinerosSugeridos.toString(),
                    bartendersSolicitados = bartendersSugeridos.toString(),
                    coperosSolicitados = coperosSugeridos.toString(),
                    menusSolicitados = if (stringMenus.isBlank()) "Sin menús" else stringMenus,
                    barrasSolicitadas = if (stringBarras.isBlank()) "Sin barras" else stringBarras,
                    detalleInsumos = tipoInsumos,
                    aceptaTerminos = aceptaTerminos
                )
                repository.crearSolicitud(nuevaSolicitud)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isGuardando = false
            }
        }
    }
}