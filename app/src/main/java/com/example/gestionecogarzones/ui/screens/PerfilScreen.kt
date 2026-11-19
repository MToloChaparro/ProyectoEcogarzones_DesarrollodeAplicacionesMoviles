package com.example.gestionecogarzones.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gestionecogarzones.data.local.entity.RolUsuario
import com.example.gestionecogarzones.data.local.entity.UsuarioEntity
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import com.example.gestionecogarzones.ui.navigation.Rutas
import com.example.gestionecogarzones.utils.FileUtils
import com.example.gestionecogarzones.viewmodel.PerfilViewModel
import com.example.gestionecogarzones.viewmodel.ViewModelFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    repository: SolicitudRepository
) {
    val viewModel: PerfilViewModel = viewModel(factory = ViewModelFactory(repository))
    val usuarioState by viewModel.usuarioActual.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var mostrarDialogoFoto by remember { mutableStateOf(false) }
    var mostrarDialogoSalir by remember { mutableStateOf(false) }
    var fotoTempUri by remember { mutableStateOf<Uri?>(null) }

    val solicitudesCliente by repository.obtenerSolicitudesPorClienteFlow(usuarioState?.email ?: "").collectAsStateWithLifecycle(initialValue = emptyList())

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && fotoTempUri != null) {
            viewModel.actualizarFoto(fotoTempUri.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            val photoFile = File.createTempFile("perfil_foto_", ".jpg", context.filesDir)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
            fotoTempUri = uri
            cameraLauncher.launch(uri)
        }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val copiaUri = FileUtils.copiarUriAArchivosInternos(context, uri, "perfil_foto")
            if (copiaUri != null) {
                viewModel.actualizarFoto(copiaUri.toString())
            }
        }
    }

    LaunchedEffect(viewModel.mensajeExito) {
        viewModel.mensajeExito?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensajeExito()
        }
    }

    val usuario = usuarioState

    if (mostrarDialogoSalir) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSalir = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas salir de tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoSalir = false
                    repository.emailUsuarioActual = ""
                    navController.navigate(Rutas.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                }) { Text("Sí, salir") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoSalir = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarDialogoFoto && usuario != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFoto = false },
            title = { Text("Foto de Perfil", fontWeight = FontWeight.Bold) },
            text = { Text("Elige una opción para actualizar o remover tu foto:") },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        mostrarDialogoFoto = false
                        if (hasCameraPermission) {
                            val photoFile = File.createTempFile("perfil_foto_", ".jpg", context.filesDir)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                            fotoTempUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cámara")
                    }

                    TextButton(onClick = {
                        mostrarDialogoFoto = false
                        galeriaLauncher.launch("image/*")
                    }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Galería")
                    }
                }
            },
            dismissButton = {
                if (!usuario.fotoUri.isNullOrBlank()) {
                    TextButton(onClick = {
                        mostrarDialogoFoto = false
                        viewModel.actualizarFoto("")
                    }) {
                        Text("Quitar Foto", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }

    if (viewModel.isEditing && usuario != null) {
        DialogEditarPerfil(viewModel = viewModel, usuario = usuario)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogoSalir = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (usuario == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Avatar
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { mostrarDialogoFoto = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!usuario.fotoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(Uri.parse(usuario.fotoUri))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = usuario.nombre.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    IconButton(
                        onClick = { mostrarDialogoFoto = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                InsigniaRolPerfil(rol = usuario.rol)

                Spacer(modifier = Modifier.height(24.dp))

                // Datos Personales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Información Personal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.iniciarEdicion(usuario) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        FilaDatoPerfil(icono = Icons.Default.Email, titulo = "Correo", valor = usuario.email)
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaDatoPerfil(icono = Icons.Default.Phone, titulo = "Teléfono", valor = usuario.telefono ?: "No especificado")
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaDatoPerfil(icono = Icons.Default.Home, titulo = "Dirección", valor = usuario.direccion ?: "No especificada")

                        if (!usuario.rut.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            FilaDatoPerfil(icono = Icons.Default.Badge, titulo = "RUT / Identificación", valor = usuario.rut)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ficha Extendida por Rol
                if (usuario.rol == RolUsuario.CLIENTE) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Preferencias y Facturación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            FilaDatoPerfil(icono = Icons.Default.CreditCard, titulo = "Método de Pago Preferido", valor = usuario.metodoPagoPreferido ?: "No especificado")
                            Spacer(modifier = Modifier.height(12.dp))
                            FilaDatoPerfil(icono = Icons.Default.Description, titulo = "Preferencias para Eventos", valor = usuario.notasPreferencia ?: "Sin observaciones registradas")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Mis Eventos y Solicitudes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (solicitudesCliente.isNotEmpty())
                                    "Has registrado ${solicitudesCliente.size} solicitud(es) de eventos con Ecogarzones."
                                else
                                    "Aún no has registrado solicitudes de eventos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { navController.navigate(Rutas.FormularioCliente.ruta) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cotizar un Nuevo Evento")
                            }
                        }
                    }
                } else if (usuario.rol == RolUsuario.TRABAJADOR) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Ficha Operativa de Personal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            FilaDatoPerfil(icono = Icons.Default.Work, titulo = "Especialidad / Cargo", valor = usuario.cargoEspecialidad ?: "Personal de Servicio")
                            Spacer(modifier = Modifier.height(12.dp))
                            FilaDatoPerfil(icono = Icons.Default.Schedule, titulo = "Disponibilidad de Turnos", valor = usuario.disponibilidadTurno ?: "Fines de semana y eventos")
                            Spacer(modifier = Modifier.height(12.dp))
                            FilaDatoPerfil(icono = Icons.Default.Checkroom, titulo = "Talla de Uniforme", valor = usuario.tallaUniforme ?: "No especificada")
                            Spacer(modifier = Modifier.height(12.dp))
                            FilaDatoPerfil(icono = Icons.Default.MedicalServices, titulo = "Carnet Manipulante / Salud", valor = usuario.carnetManipulador ?: "Al día")
                            Spacer(modifier = Modifier.height(12.dp))
                            FilaDatoPerfil(icono = Icons.Default.ContactPhone, titulo = "Contacto de Emergencia", valor = usuario.contactoEmergencia ?: "No registrado")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Gestión de Personal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Acceso activo a la plataforma de administración de inventarios, eventos e incidencias asignadas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = { navController.navigate(Rutas.PanelAdmin.ruta) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Dashboard, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ir al Panel de Administración")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DialogEditarPerfil(
    viewModel: PerfilViewModel,
    usuario: UsuarioEntity
) {
    AlertDialog(
        onDismissRequest = viewModel::cancelarEdicion,
        title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = viewModel.nombreEdit,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = viewModel.telefonoEdit,
                    onValueChange = viewModel::onTelefonoChange,
                    label = { Text("Teléfono") },
                    placeholder = { Text("+56 9 1234 5678") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = viewModel.direccionEdit,
                    onValueChange = viewModel::onDireccionChange,
                    label = { Text("Dirección") },
                    placeholder = { Text("Calle 123, Ciudad") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = viewModel.rutEdit,
                    onValueChange = viewModel::onRutChange,
                    label = { Text("RUT / Identificación") },
                    placeholder = { Text("12.345.678-9") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (usuario.rol == RolUsuario.CLIENTE) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.metodoPagoEdit,
                        onValueChange = viewModel::onMetodoPagoChange,
                        label = { Text("Método de Pago Preferido") },
                        placeholder = { Text("Transferencia / Tarjeta / Factura") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.notasPreferenciaEdit,
                        onValueChange = viewModel::onNotasPreferenciaChange,
                        label = { Text("Notas de Preferencia") },
                        placeholder = { Text("Preferencias de menús, protocolo, etc.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else if (usuario.rol == RolUsuario.TRABAJADOR) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.cargoEspecialidadEdit,
                        onValueChange = viewModel::onCargoChange,
                        label = { Text("Cargo / Especialidad") },
                        placeholder = { Text("Garzón Principal / Bartender") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.disponibilidadTurnoEdit,
                        onValueChange = viewModel::onDisponibilidadTurnoChange,
                        label = { Text("Disponibilidad de Turnos") },
                        placeholder = { Text("Fines de semana / Noches") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.tallaUniformeEdit,
                        onValueChange = viewModel::onTallaUniformeChange,
                        label = { Text("Talla de Uniforme") },
                        placeholder = { Text("M, L, XL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.contactoEmergenciaEdit,
                        onValueChange = viewModel::onContactoEmergenciaChange,
                        label = { Text("Contacto de Emergencia") },
                        placeholder = { Text("Nombre y Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.carnetManipuladorEdit,
                        onValueChange = viewModel::onCarnetManipuladorChange,
                        label = { Text("Carnet de Manipulante / Salud") },
                        placeholder = { Text("Vigente hasta 2027 / Al día") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = viewModel::guardarCambios,
                enabled = !viewModel.isSaving && viewModel.nombreEdit.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::cancelarEdicion) { Text("Cancelar") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun FilaDatoPerfil(icono: ImageVector, titulo: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = titulo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun InsigniaRolPerfil(rol: RolUsuario) {
    val infoRol = when (rol) {
        RolUsuario.ADMINISTRADOR -> RolInfoPerfil(
            "Administrador del Sistema",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.Default.AdminPanelSettings
        )
        RolUsuario.TRABAJADOR -> RolInfoPerfil(
            "Personal / Garzón de Banquetería",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Default.Badge
        )
        RolUsuario.CLIENTE -> RolInfoPerfil(
            "Cliente VIP",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            Icons.Default.Star
        )
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = infoRol.colorFondo
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = infoRol.icono,
                contentDescription = null,
                tint = infoRol.colorTexto,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = infoRol.etiqueta,
                style = MaterialTheme.typography.labelLarge,
                color = infoRol.colorTexto,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class RolInfoPerfil(
    val etiqueta: String,
    val colorFondo: Color,
    val colorTexto: Color,
    val icono: ImageVector
)
