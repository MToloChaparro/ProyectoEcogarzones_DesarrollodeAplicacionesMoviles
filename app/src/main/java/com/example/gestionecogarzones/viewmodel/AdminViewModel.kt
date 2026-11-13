package com.example.gestionecogarzones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionecogarzones.data.local.entity.EstadoEvento
import com.example.gestionecogarzones.data.local.entity.EstadoIncidencia
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import com.example.gestionecogarzones.data.local.entity.UsuarioEntity
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: SolicitudRepository
) : ViewModel() {

    val usuarioActual: StateFlow<UsuarioEntity?> = repository.obtenerUsuarioFlow(repository.emailUsuarioActual)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val solicitudes: StateFlow<List<SolicitudEntity>> = repository.todasLasSolicitudes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incidencias: StateFlow<List<IncidenciaEntity>> = repository.todasLasIncidencias
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventario: StateFlow<List<InventarioEntity>> = repository.todoElInventario
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trabajadores: StateFlow<List<UsuarioEntity>> = repository.todosLosTrabajadores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalEventos = solicitudes.map { it.size }
    val eventosPendientes = solicitudes.map { lista -> lista.count { it.estado == EstadoEvento.PENDIENTE } }
    val totalInvitados = solicitudes.map { lista -> lista.sumOf { it.cantidadInvitados } }

    val valorTotalInventario = inventario.map { lista -> lista.sumOf { it.cantidad * it.costoUnidad } }
    val eventosActivos = solicitudes.map { lista ->
        lista.count { it.estado == EstadoEvento.CONFIRMADO || it.estado == EstadoEvento.EN_PROCESO }
    }
    val incidenciasPendientes = incidencias.map { lista ->
        lista.count { it.estado != EstadoIncidencia.RESUELTA }
    }
    val invitadosConfirmados = solicitudes.map { lista ->
        lista.filter { it.estado == EstadoEvento.CONFIRMADO }.sumOf { it.cantidadInvitados }
    }

    fun cambiarEstado(solicitud: SolicitudEntity, nuevoEstado: EstadoEvento) {
        viewModelScope.launch {
            repository.actualizarSolicitud(solicitud.copy(estado = nuevoEstado))
        }
    }

    fun asignarPersonal(solicitud: SolicitudEntity, personalNombres: String) {
        viewModelScope.launch {
            repository.asignarPersonalAEvento(solicitud, personalNombres)
        }
    }

    fun guardarChecklist(solicitud: SolicitudEntity, checklistData: String) {
        viewModelScope.launch {
            repository.guardarChecklistInsumos(solicitud, checklistData)
        }
    }

    fun guardarFeedback(solicitud: SolicitudEntity, estrellas: Int, comentario: String) {
        viewModelScope.launch {
            repository.guardarFeedback(solicitud, estrellas, comentario)
        }
    }

    fun guardarIncidencia(solicitudId: Long, tipo: String, descripcion: String, fotoUri: String? = null) {
        viewModelScope.launch {
            val nuevaIncidencia = IncidenciaEntity(
                solicitudId = solicitudId,
                tipo = tipo,
                descripcion = descripcion,
                fotoUri = fotoUri
            )
            repository.registrarIncidencia(nuevaIncidencia)
        }
    }

    fun cambiarEstadoIncidencia(incidencia: IncidenciaEntity, nuevoEstado: EstadoIncidencia) {
        viewModelScope.launch {
            repository.actualizarIncidencia(incidencia.copy(estado = nuevoEstado))
        }
    }

    fun actualizarStock(item: InventarioEntity, nuevaCantidad: Int) {
        viewModelScope.launch {
            repository.actualizarInventario(item.copy(cantidad = nuevaCantidad))
        }
    }

    fun actualizarItemCompleto(item: InventarioEntity) {
        viewModelScope.launch {
            repository.actualizarInventario(item)
        }
    }

    fun agregarItemInventario(
        nombre: String,
        categoria: String,
        cantidad: Int,
        costo: Double,
        proveedor: String,
        unidad: String
    ) {
        viewModelScope.launch {
            val nuevoItem = InventarioEntity(
                nombreItem = nombre,
                categoria = categoria,
                cantidad = cantidad,
                costoUnidad = costo,
                proveedor = proveedor,
                unidad = unidad
            )
            repository.agregarInventario(nuevoItem)
        }
    }
}