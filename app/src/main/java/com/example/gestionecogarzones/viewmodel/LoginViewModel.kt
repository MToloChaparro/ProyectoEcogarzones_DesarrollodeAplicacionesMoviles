package com.example.gestionecogarzones.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionecogarzones.data.local.entity.RolUsuario
import com.example.gestionecogarzones.data.local.entity.UsuarioEntity
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: SolicitudRepository
) : ViewModel() {

    var esModoRegistro by mutableStateOf(false)
        private set

    var nombreRegistro by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set

    var passwordVisible by mutableStateOf(false)
        private set
    var confirmPasswordVisible by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    val rolDetectado: RolUsuario
        get() = determinarRolPorEmail(email)

    fun setModoRegistro(esRegistro: Boolean) {
        esModoRegistro = esRegistro
        errorMessage = null
    }

    fun onNombreChange(value: String) {
        nombreRegistro = value
        errorMessage = null
    }

    fun onEmailChange(value: String) {
        email = value
        errorMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    fun onConfirmPasswordChange(value: String) {
        confirmPassword = value
        errorMessage = null
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    fun determinarRolPorEmail(emailText: String): RolUsuario {
        val clean = emailText.trim().lowercase()
        return when {
            clean.endsWith("@adminecogarzones.com") -> RolUsuario.ADMINISTRADOR
            clean.endsWith("@personalecogarzones.com") -> RolUsuario.TRABAJADOR
            else -> RolUsuario.CLIENTE
        }
    }

    fun iniciarSesion(onSuccess: (RolUsuario) -> Unit) {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = password.trim()

        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            errorMessage = "Por favor ingresa tu correo y contraseña"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val usuario = repository.autenticarUsuario(cleanEmail, cleanPass)

                if (usuario == null) {
                    val existente = repository.obtenerUsuarioPorEmail(cleanEmail)
                    if (existente != null) {
                        errorMessage = "Contraseña incorrecta"
                    } else {
                        errorMessage = "El usuario no existe. Regístrate para crear una cuenta."
                    }
                    return@launch
                }

                repository.emailUsuarioActual = usuario.email
                onSuccess(usuario.rol)
            } catch (e: Exception) {
                errorMessage = "Error de inicio de sesión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun registrarCuenta(onSuccess: (RolUsuario) -> Unit) {
        val cleanNombre = nombreRegistro.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPass = password.trim()
        val cleanConfirm = confirmPassword.trim()

        if (cleanNombre.isBlank() || cleanEmail.isBlank() || cleanPass.isBlank() || cleanConfirm.isBlank()) {
            errorMessage = "Por favor completa todos los campos"
            return
        }

        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            errorMessage = "Por favor ingresa un correo electrónico válido"
            return
        }

        if (cleanPass.length < 4) {
            errorMessage = "La contraseña debe tener al menos 4 caracteres"
            return
        }

        if (cleanPass != cleanConfirm) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val existente = repository.obtenerUsuarioPorEmail(cleanEmail)
                if (existente != null) {
                    errorMessage = "Este correo electrónico ya está registrado"
                    return@launch
                }

                val rolCalculado = determinarRolPorEmail(cleanEmail)
                val nuevoUsuario = UsuarioEntity(
                    nombre = cleanNombre,
                    email = cleanEmail,
                    password = cleanPass,
                    rol = rolCalculado
                )
                repository.registrarUsuario(nuevoUsuario)
                repository.emailUsuarioActual = cleanEmail
                onSuccess(rolCalculado)
            } catch (e: Exception) {
                errorMessage = "Error al registrar la cuenta: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}