package com.example.gestionecogarzones.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionecogarzones.data.local.entity.UsuarioEntity
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: SolicitudRepository
) : ViewModel() {

    val usuarioActual: StateFlow<UsuarioEntity?> = repository.obtenerUsuarioFlow(repository.emailUsuarioActual)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todasLasSolicitudes = repository.todasLasSolicitudes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var isEditing by mutableStateOf(false)
        private set

    var nombreEdit by mutableStateOf("")
        private set
    var telefonoEdit by mutableStateOf("")
        private set
    var direccionEdit by mutableStateOf("")
        private set
    var cargoEspecialidadEdit by mutableStateOf("")
        private set
    var rutEdit by mutableStateOf("")
        private set
    var metodoPagoEdit by mutableStateOf("")
        private set
    var notasPreferenciaEdit by mutableStateOf("")
        private set
    var disponibilidadTurnoEdit by mutableStateOf("")
        private set
    var tallaUniformeEdit by mutableStateOf("")
        private set
    var contactoEmergenciaEdit by mutableStateOf("")
        private set
    var carnetManipuladorEdit by mutableStateOf("")
        private set

    var isSaving by mutableStateOf(false)
        private set
    var mensajeExito by mutableStateOf<String?>(null)
        private set

    fun iniciarEdicion(usuario: UsuarioEntity) {
        nombreEdit = usuario.nombre
        telefonoEdit = usuario.telefono ?: ""
        direccionEdit = usuario.direccion ?: ""
        cargoEspecialidadEdit = usuario.cargoEspecialidad ?: ""
        rutEdit = usuario.rut ?: ""
        metodoPagoEdit = usuario.metodoPagoPreferido ?: ""
        notasPreferenciaEdit = usuario.notasPreferencia ?: ""
        disponibilidadTurnoEdit = usuario.disponibilidadTurno ?: ""
        tallaUniformeEdit = usuario.tallaUniforme ?: ""
        contactoEmergenciaEdit = usuario.contactoEmergencia ?: ""
        carnetManipuladorEdit = usuario.carnetManipulador ?: ""
        isEditing = true
        mensajeExito = null
    }

    fun cancelarEdicion() {
        isEditing = false
    }

    fun onNombreChange(value: String) { nombreEdit = value }
    fun onTelefonoChange(value: String) { telefonoEdit = value }
    fun onDireccionChange(value: String) { direccionEdit = value }
    fun onCargoChange(value: String) { cargoEspecialidadEdit = value }
    fun onRutChange(value: String) { rutEdit = value }
    fun onMetodoPagoChange(value: String) { metodoPagoEdit = value }
    fun onNotasPreferenciaChange(value: String) { notasPreferenciaEdit = value }
    fun onDisponibilidadTurnoChange(value: String) { disponibilidadTurnoEdit = value }
    fun onTallaUniformeChange(value: String) { tallaUniformeEdit = value }
    fun onContactoEmergenciaChange(value: String) { contactoEmergenciaEdit = value }
    fun onCarnetManipuladorChange(value: String) { carnetManipuladorEdit = value }

    fun actualizarFoto(fotoUri: String) {
        val u = usuarioActual.value ?: return
        viewModelScope.launch {
            repository.actualizarUsuario(u.copy(fotoUri = fotoUri))
            mensajeExito = "Foto de perfil actualizada"
        }
    }

    fun guardarCambios() {
        val u = usuarioActual.value ?: return
        if (nombreEdit.isBlank()) return

        viewModelScope.launch {
            isSaving = true
            try {
                val usuarioActualizado = u.copy(
                    nombre = nombreEdit.trim(),
                    telefono = telefonoEdit.trim().ifBlank { null },
                    direccion = direccionEdit.trim().ifBlank { null },
                    cargoEspecialidad = cargoEspecialidadEdit.trim().ifBlank { null },
                    rut = rutEdit.trim().ifBlank { null },
                    metodoPagoPreferido = metodoPagoEdit.trim().ifBlank { null },
                    notasPreferencia = notasPreferenciaEdit.trim().ifBlank { null },
                    disponibilidadTurno = disponibilidadTurnoEdit.trim().ifBlank { null },
                    tallaUniforme = tallaUniformeEdit.trim().ifBlank { null },
                    contactoEmergencia = contactoEmergenciaEdit.trim().ifBlank { null },
                    carnetManipulador = carnetManipuladorEdit.trim().ifBlank { null }
                )
                repository.actualizarUsuario(usuarioActualizado)
                isEditing = false
                mensajeExito = "Perfil actualizado exitosamente"
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSaving = false
            }
        }
    }

    fun limpiarMensajeExito() {
        mensajeExito = null
    }
}
