package com.example.gestionecogarzones.ui.screens.admin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gestionecogarzones.data.local.entity.EstadoEvento
import com.example.gestionecogarzones.data.local.entity.EstadoIncidencia
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.RolUsuario
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import com.example.gestionecogarzones.data.local.entity.UsuarioEntity
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import com.example.gestionecogarzones.ui.navigation.Rutas
import com.example.gestionecogarzones.utils.NotificationHelper
import com.example.gestionecogarzones.viewmodel.AdminViewModel
import com.example.gestionecogarzones.viewmodel.ViewModelFactory
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelAdminScreen(
    navController: NavController,
    repository: SolicitudRepository
) {
    val viewModel: AdminViewModel = viewModel(factory = ViewModelFactory(repository))

    // PRUEBA DE CONEXIÓN
    LaunchedEffect(Unit) {
        viewModel.probarConexion()
    }

    val usuarioActual by viewModel.usuarioActual.collectAsStateWithLifecycle()
    val esAdmin = usuarioActual?.rol == RolUsuario.ADMINISTRADOR

    val listaSolicitudes by viewModel.solicitudes.collectAsStateWithLifecycle()
    val listaIncidencias by viewModel.incidencias.collectAsStateWithLifecycle()
    val listaInventario by viewModel.inventario.collectAsStateWithLifecycle()
    val listaUsuarios by viewModel.usuarios.collectAsStateWithLifecycle()

    var pestanaSeleccionada by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (esAdmin) "Administración General" else "Panel de Personal",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Rutas.Inicio.ruta) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Rutas.Perfil.ruta) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        navController.navigate(Rutas.Login.ruta) { popUpTo(0) { inclusive = true } }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            PrimaryTabRow(
                selectedTabIndex = pestanaSeleccionada,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(selected = pestanaSeleccionada == 0, onClick = { pestanaSeleccionada = 0 }, text = { Text("Resumen", fontWeight = FontWeight.Bold) })
                Tab(selected = pestanaSeleccionada == 1, onClick = { pestanaSeleccionada = 1 }, text = { Text("Eventos", fontWeight = FontWeight.Bold) })
                Tab(selected = pestanaSeleccionada == 2, onClick = { pestanaSeleccionada = 2 }, text = { Text("Incidencias", fontWeight = FontWeight.Bold) })
                Tab(selected = pestanaSeleccionada == 3, onClick = { pestanaSeleccionada = 3 }, text = { Text("Inventario", fontWeight = FontWeight.Bold) })
                if (esAdmin) {
                    Tab(selected = pestanaSeleccionada == 4, onClick = { pestanaSeleccionada = 4 }, text = { Text("Usuarios", fontWeight = FontWeight.Bold) })
                }
            }

            when (pestanaSeleccionada) {
                0 -> VistaResumen(viewModel, esAdmin)
                1 -> VistaEventos(listaSolicitudes, viewModel)
                2 -> VistaIncidencias(listaIncidencias, viewModel)
                3 -> VistaInventario(listaInventario, viewModel, esAdmin)
                4 -> if (esAdmin) VistaUsuarios(listaUsuarios)
            }
        }
    }
}

@Composable
fun VistaResumen(viewModel: AdminViewModel, esAdmin: Boolean) {
    val totalEventos by viewModel.totalEventos.collectAsState(initial = 0)
    val eventosPendientes by viewModel.eventosPendientes.collectAsState(initial = 0)
    val totalInvitados by viewModel.totalInvitados.collectAsState(initial = 0)
    val valorInventario by viewModel.valorTotalInventario.collectAsState(initial = 0.0)
    val eventosActivos by viewModel.eventosActivos.collectAsState(initial = 0)
    val incidenciasPendientes by viewModel.incidenciasPendientes.collectAsState(initial = 0)

    val textoValorFinanciero = "$${String.format(Locale.getDefault(), "%,.0f", valorInventario)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (esAdmin) "Resumen Financiero y Operativo" else "Resumen Operativo de Terreno",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (esAdmin) {
            TarjetaMetrica("Valor Total Inventario", textoValorFinanciero, Icons.Default.AttachMoney, MaterialTheme.colorScheme.primaryContainer, Modifier.fillMaxWidth())
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TarjetaMetrica("Total Eventos", totalEventos.toString(), Icons.AutoMirrored.Filled.EventNote, MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
            TarjetaMetrica("Pendientes", eventosPendientes.toString(), Icons.Default.HourglassEmpty, Color(0xFFFFF3E0), Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TarjetaMetrica("Eventos Activos", eventosActivos.toString(), Icons.Default.PlayCircle, Color(0xFFE3F2FD), Modifier.weight(1f))
            TarjetaMetrica("Incidencias", incidenciasPendientes.toString(), Icons.Default.Warning, Color(0xFFFFEBEE), Modifier.weight(1f))
        }

        TarjetaMetrica("Total Invitados Atendidos", totalInvitados.toString(), Icons.Default.Groups, MaterialTheme.colorScheme.tertiaryContainer, Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(44.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (esAdmin) "¡Control Financiero y Logístico!" else "¡Control Operativo de Terreno!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (esAdmin)
                        "Inventario valorizado en $textoValorFinanciero con $eventosPendientes cotizaciones por confirmar."
                    else
                        "Gestión activa de $eventosActivos eventos en terreno con $totalInvitados invitados acumulados.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VistaEventos(lista: List<SolicitudEntity>, viewModel: AdminViewModel) {
    if (lista.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay solicitudes registradas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(lista) { solicitud -> TarjetaSolicitud(solicitud, viewModel) }
        }
    }
}

@Composable
fun VistaIncidencias(lista: List<IncidenciaEntity>, viewModel: AdminViewModel) {
    if (lista.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay incidencias reportadas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(lista) { incidencia -> TarjetaIncidencia(incidencia, viewModel) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaInventario(
    lista: List<InventarioEntity>,
    viewModel: AdminViewModel,
    esAdmin: Boolean
) {
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    var itemAEditar by remember { mutableStateOf<InventarioEntity?>(null) }

    if (mostrarDialogoAgregar && esAdmin) {
        DialogoAgregarInventario(
            onDismiss = { mostrarDialogoAgregar = false },
            onAgregar = { nombre, cat, cant, costo, prov, un ->
                viewModel.agregarItemInventario(nombre, cat, cant, costo, prov, un)
                mostrarDialogoAgregar = false
            }
        )
    }

    if (itemAEditar != null && esAdmin) {
        DialogoEditarInventario(
            item = itemAEditar!!,
            onDismiss = { itemAEditar = null },
            onGuardar = { itemActualizado ->
                viewModel.actualizarItemCompleto(itemActualizado)
                itemAEditar = null
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (lista.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("El inventario está vacío", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lista) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.nombreItem, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${item.categoria} (${item.unidad})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (esAdmin) {
                                    Button(
                                        onClick = { itemAEditar = item },
                                        modifier = Modifier.height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Editar", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Stock: ", style = MaterialTheme.typography.bodyMedium)
                                    FilledTonalIconButton(
                                        onClick = { if (item.cantidad > 0) viewModel.actualizarStock(item, item.cantidad - 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) { Icon(Icons.Default.Remove, contentDescription = null) }
                                    Text("${item.cantidad}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                    FilledTonalIconButton(
                                        onClick = { viewModel.actualizarStock(item, item.cantidad + 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) { Icon(Icons.Default.Add, contentDescription = null) }
                                }
                                Text("Estado: ${item.estado}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (item.cantidad > 0) Color(0xFF4CAF50) else Color.Red)
                            }

                            if (esAdmin) {
                                if (item.proveedor.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Proveedor: ${item.proveedor}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Costo Unitario: $${String.format(Locale.getDefault(), "%,.0f", item.costoUnidad)}", style = MaterialTheme.typography.bodySmall)
                                    Text("Valor Total: $${String.format(Locale.getDefault(), "%,.0f", item.valorTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (esAdmin) {
            FloatingActionButton(
                onClick = { mostrarDialogoAgregar = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ítem", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun DialogoAgregarInventario(
    onDismiss: () -> Unit,
    onAgregar: (nombre: String, cat: String, cant: Int, costo: Double, prov: String, un: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Vajilla") }
    var cantidad by remember { mutableStateOf("10") }
    var costoUnidad by remember { mutableStateOf("1000") }
    var proveedor by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("Unidades") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Ítem a Inventario", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Ítem") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría (Vajilla, Cristalería, etc.)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad (Unidades, Paquetes, Cajas)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = cantidad, onValueChange = { if (it.all { c -> c.isDigit() }) cantidad = it }, label = { Text("Cantidad / Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = costoUnidad, onValueChange = { if (it.all { c -> c.isDigit() }) costoUnidad = it }, label = { Text("Costo Unitario ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = proveedor, onValueChange = { proveedor = it }, label = { Text("Proveedor") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cantInt = cantidad.toIntOrNull() ?: 1
                    val costoDbl = costoUnidad.toDoubleOrNull() ?: 0.0
                    onAgregar(nombre, categoria, cantInt, costoDbl, proveedor, unidad)
                },
                enabled = nombre.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun DialogoEditarInventario(
    item: InventarioEntity,
    onDismiss: () -> Unit,
    onGuardar: (InventarioEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(item.nombreItem) }
    var categoria by remember { mutableStateOf(item.categoria) }
    var unidad by remember { mutableStateOf(item.unidad) }
    var cantidad by remember { mutableStateOf(item.cantidad.toString()) }
    var costoUnidad by remember { mutableStateOf(item.costoUnidad.toInt().toString()) }
    var proveedor by remember { mutableStateOf(item.proveedor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Ítem de Inventario", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Ítem") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad de Medida") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = cantidad, onValueChange = { if (it.all { c -> c.isDigit() }) cantidad = it }, label = { Text("Cantidad / Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = costoUnidad, onValueChange = { if (it.all { c -> c.isDigit() }) costoUnidad = it }, label = { Text("Costo Unitario ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = proveedor, onValueChange = { proveedor = it }, label = { Text("Proveedor") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cantInt = cantidad.toIntOrNull() ?: item.cantidad
                    val costoDbl = costoUnidad.toDoubleOrNull() ?: item.costoUnidad
                    val itemModificado = item.copy(
                        nombreItem = nombre,
                        categoria = categoria,
                        unidad = unidad,
                        cantidad = cantInt,
                        costoUnidad = costoDbl,
                        proveedor = proveedor
                    )
                    onGuardar(itemModificado)
                },
                enabled = nombre.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Guardar Cambios") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun TarjetaIncidencia(incidencia: IncidenciaEntity, viewModel: AdminViewModel) {
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarFotoCompleta by remember { mutableStateOf(false) }

    val (colorEstado, nombreEstado) = when (incidencia.estado) {
        EstadoIncidencia.REPORTADA -> Color(0xFFF44336) to "REPORTADA"
        EstadoIncidencia.EN_PROCESO -> Color(0xFFFFA500) to "EN PROCESO"
        EstadoIncidencia.RESUELTA -> Color(0xFF4CAF50) to "RESUELTA"
    }

    if (mostrarFotoCompleta && !incidencia.fotoUri.isNullOrBlank()) {
        Dialog(onDismissRequest = { mostrarFotoCompleta = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(Uri.parse(incidencia.fotoUri)).crossfade(true).build(), contentDescription = "Evidencia", modifier = Modifier.fillMaxSize().padding(16.dp), contentScale = ContentScale.Fit)
                IconButton(onClick = { mostrarFotoCompleta = false }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tipo: ${incidencia.tipo}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Box {
                    Text(text = nombreEstado, color = colorEstado, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(colorEstado.copy(alpha = 0.15f)).clickable { menuExpandido = true }.padding(horizontal = 8.dp, vertical = 4.dp))
                    DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                        EstadoIncidencia.entries.forEach { estado -> DropdownMenuItem(text = { Text(estado.name) }, onClick = { viewModel.cambiarEstadoIncidencia(incidencia, estado); menuExpandido = false }) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(incidencia.descripcion, style = MaterialTheme.typography.bodyMedium)
            if (!incidencia.fotoUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).clickable { mostrarFotoCompleta = true }, contentAlignment = Alignment.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(Uri.parse(incidencia.fotoUri)).crossfade(true).build(), contentDescription = "Evidencia", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Icon(Icons.Default.ZoomIn, contentDescription = "Ampliar", tint = Color.White, modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape).padding(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Evento ID: #${incidencia.solicitudId}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun TarjetaMetrica(titulo: String, valor: String, icono: ImageVector, colorFondo: Color, modifier: Modifier = Modifier) {
    val esFondoOscuro = colorFondo == MaterialTheme.colorScheme.primaryContainer || colorFondo == MaterialTheme.colorScheme.secondaryContainer
    val colorTexto = if (esFondoOscuro) Color.White else Color.Black
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colorFondo)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) { Icon(icono, contentDescription = null, tint = colorTexto) }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = colorTexto)
            Text(text = titulo, style = MaterialTheme.typography.bodySmall, color = colorTexto.copy(alpha = 0.8f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaSolicitud(solicitud: SolicitudEntity, viewModel: AdminViewModel) {
    val context = LocalContext.current
    var mostrarDialogoIncidencia by remember { mutableStateOf(false) }
    var mostrarDialogoPersonal by remember { mutableStateOf(false) }
    var mostrarDialogoChecklist by remember { mutableStateOf(false) }
    var mostrarDialogoFeedback by remember { mutableStateOf(false) }

    var textoIncidencia by remember { mutableStateOf("") }
    var tipoIncidencia by remember { mutableStateOf("Logística") }
    var expandirMenuTipo by remember { mutableStateOf(false) }
    var fotoAdjuntaUri by remember { mutableStateOf<Uri?>(null) }
    var menuEstadoExpandido by remember { mutableStateOf(false) }

    val listaTrabajadores by viewModel.trabajadores.collectAsState()
    var personalSeleccionado by remember { mutableStateOf(solicitud.garzonesAsignados ?: "") }

    var chkVajilla by remember { mutableStateOf(solicitud.checklistInsumos?.contains("Vajilla") == true) }
    var chkCristaleria by remember { mutableStateOf(solicitud.checklistInsumos?.contains("Cristalería") == true) }
    var chkManteleria by remember { mutableStateOf(solicitud.checklistInsumos?.contains("Mantelería") == true) }
    var chkUniformes by remember { mutableStateOf(solicitud.checklistInsumos?.contains("Uniformes") == true) }

    var estrellasFeedback by remember { mutableIntStateOf(solicitud.calificacionEstrellas ?: 5) }
    var comentarioFeedback by remember { mutableStateOf(solicitud.comentarioFeedback ?: "") }

    var hasCameraPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) Handler(Looper.getMainLooper()).postDelayed({ mostrarDialogoIncidencia = true }, 500) else fotoAdjuntaUri = null
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) abrirCamaraParaIncidencia(context, solicitud, cameraLauncher) { fotoAdjuntaUri = it }
    }
    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) { fotoAdjuntaUri = uri; mostrarDialogoIncidencia = true }
    }

    val (colorEstado, iconoEstado) = when (solicitud.estado) {
        EstadoEvento.PENDIENTE -> Color(0xFFFFA500) to Icons.Default.HourglassEmpty
        EstadoEvento.CONFIRMADO -> Color(0xFF4CAF50) to Icons.Default.CheckCircle
        EstadoEvento.EN_PROCESO -> Color(0xFF2196F3) to Icons.Default.PlayCircle
        EstadoEvento.FINALIZADO -> Color.Gray to Icons.Default.DoneAll
        EstadoEvento.CANCELADO -> Color.Red to Icons.Default.Cancel
    }

    if (mostrarDialogoPersonal) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPersonal = false },
            title = { Text("Asignar Personal / Turnos", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Selecciona el equipo asignado a este evento:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (listaTrabajadores.isEmpty()) {
                        Text("No hay personal registrado en el sistema.", color = Color.Gray)
                    } else {
                        listaTrabajadores.forEach { trabajador ->
                            val estaSeleccionado = personalSeleccionado.contains(trabajador.nombre)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        personalSeleccionado = if (estaSeleccionado) {
                                            personalSeleccionado.replace(trabajador.nombre, "").replace(", ,", ",").trim(',', ' ')
                                        } else {
                                            if (personalSeleccionado.isBlank()) trabajador.nombre else "$personalSeleccionado, ${trabajador.nombre}"
                                        }
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(checked = estaSeleccionado, onCheckedChange = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(trabajador.nombre, fontWeight = FontWeight.Bold)
                                    Text(trabajador.cargoEspecialidad ?: "Personal", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.asignarPersonal(solicitud, personalSeleccionado)
                    mostrarDialogoPersonal = false
                    NotificationHelper.enviarNotificacion(
                        context,
                        "Turnos Asignados",
                        "Se asignó el personal a la solicitud de ${solicitud.nombreCliente}"
                    )
                    Toast.makeText(context, "Personal asignado correctamente", Toast.LENGTH_SHORT).show()
                }) { Text("Guardar Asignación") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoPersonal = false }) { Text("Cancelar") } }
        )
    }

    if (mostrarDialogoChecklist) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoChecklist = false },
            title = { Text("Checklist Pre-Evento (Control)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Verifica la salida de insumos y preparación:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = chkVajilla, onCheckedChange = { chkVajilla = it })
                        Text("Vajilla y platos embalados")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = chkCristaleria, onCheckedChange = { chkCristaleria = it })
                        Text("Cristalería y copas sanitizadas")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = chkManteleria, onCheckedChange = { chkManteleria = it })
                        Text("Mantelería limpia y planchada")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = chkUniformes, onCheckedChange = { chkUniformes = it })
                        Text("Uniformes e higiene del equipo ok")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val listaChk = mutableListOf<String>()
                    if (chkVajilla) listaChk.add("Vajilla")
                    if (chkCristaleria) listaChk.add("Cristalería")
                    if (chkManteleria) listaChk.add("Mantelería")
                    if (chkUniformes) listaChk.add("Uniformes")

                    val resultadoStr = if (listaChk.isEmpty()) "Pendiente" else listaChk.joinToString(", ")
                    viewModel.guardarChecklist(solicitud, resultadoStr)
                    mostrarDialogoChecklist = false
                    Toast.makeText(context, "Checklist guardado", Toast.LENGTH_SHORT).show()
                }) { Text("Guardar Checklist") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoChecklist = false }) { Text("Cancelar") } }
        )
    }

    if (mostrarDialogoFeedback) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFeedback = false },
            title = { Text("Calificación del Cliente", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Calificación del servicio (1 a 5 estrellas):", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        (1..5).forEach { num ->
                            IconButton(onClick = { estrellasFeedback = num }) {
                                Icon(
                                    imageVector = if (num <= estrellasFeedback) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = comentarioFeedback,
                        onValueChange = { comentarioFeedback = it },
                        label = { Text("Comentario o reseña del cliente") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.guardarFeedback(solicitud, estrellasFeedback, comentarioFeedback)
                    mostrarDialogoFeedback = false
                    Toast.makeText(context, "Feedback guardado", Toast.LENGTH_SHORT).show()
                }) { Text("Guardar Reseña") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoFeedback = false }) { Text("Cancelar") } }
        )
    }

    if (mostrarDialogoIncidencia) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoIncidencia = false; fotoAdjuntaUri = null; textoIncidencia = "" },
            title = { Text("Reportar Incidencia", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    ExposedDropdownMenuBox(expanded = expandirMenuTipo, onExpandedChange = { expandirMenuTipo = it }) {
                        OutlinedTextField(value = tipoIncidencia, onValueChange = {}, readOnly = true, label = { Text("Tipo de incidencia") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMenuTipo) }, modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        ExposedDropdownMenu(expanded = expandirMenuTipo, onDismissRequest = { expandirMenuTipo = false }) {
                            listOf("Logística", "Personal", "Inventario", "Cliente", "Otro").forEach { tipo -> DropdownMenuItem(text = { Text(tipo) }, onClick = { tipoIncidencia = tipo; expandirMenuTipo = false }) }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = textoIncidencia, onValueChange = { textoIncidencia = it }, label = { Text("Describe el problema...") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 2)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (fotoAdjuntaUri != null) {
                        Text("Evidencia adjunta:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(fotoAdjuntaUri).crossfade(true).build(), contentDescription = "Foto", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { if (hasCameraPermission) { abrirCamaraParaIncidencia(context, solicitud, cameraLauncher) { fotoAdjuntaUri = it } } else { permissionLauncher.launch(Manifest.permission.CAMERA) } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("Cámara", fontSize = 13.sp)
                        }
                        OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("Galería", fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.guardarIncidencia(solicitudId = solicitud.id, tipo = tipoIncidencia, descripcion = textoIncidencia, fotoUri = fotoAdjuntaUri?.toString()); mostrarDialogoIncidencia = false; textoIncidencia = ""; fotoAdjuntaUri = null; Toast.makeText(context, "Incidencia guardada", Toast.LENGTH_SHORT).show() }, enabled = textoIncidencia.isNotBlank(), shape = RoundedCornerShape(8.dp)) { Text("Enviar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoIncidencia = false; fotoAdjuntaUri = null }) { Text("Cancelar") } },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) { Text("Cliente", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(solicitud.nombreCliente, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                Box {
                    Row(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(colorEstado.copy(alpha = 0.15f)).clickable { menuEstadoExpandido = true }.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(iconoEstado, contentDescription = null, tint = colorEstado, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(6.dp)); Text(solicitud.estado.name, color = colorEstado, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold); Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = colorEstado, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = menuEstadoExpandido, onDismissRequest = { menuEstadoExpandido = false }) {
                        EstadoEvento.entries.forEach { estado -> DropdownMenuItem(text = { Text(estado.name) }, onClick = { viewModel.cambiarEstado(solicitud, estado); menuEstadoExpandido = false }) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(16.dp))
            FilaDetalle(icono = Icons.Default.Celebration, texto = solicitud.tipoEvento)
            Spacer(modifier = Modifier.height(8.dp))
            FilaDetalle(icono = Icons.Default.CalendarMonth, texto = solicitud.fechaEvento)
            Spacer(modifier = Modifier.height(8.dp))
            FilaDetalle(icono = Icons.Default.Groups, texto = "${solicitud.cantidadInvitados} invitados")
            Spacer(modifier = Modifier.height(8.dp))
            FilaDetalle(icono = Icons.Default.LocationOn, texto = solicitud.direccion)

            if (!solicitud.garzonesAsignados.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                FilaDetalle(icono = Icons.Default.Badge, texto = "Personal: ${solicitud.garzonesAsignados}", color = MaterialTheme.colorScheme.primary)
            }

            if (!solicitud.checklistInsumos.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                FilaDetalle(icono = Icons.AutoMirrored.Filled.FactCheck, texto = "Checklist: ${solicitud.checklistInsumos}", color = MaterialTheme.colorScheme.secondary)
            }

            if (solicitud.calificacionEstrellas != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${solicitud.calificacionEstrellas} / 5  — \"${solicitud.comentarioFeedback ?: ""}\"", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Gestión & Operaciones", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { mostrarDialogoPersonal = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Turnos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(onClick = { mostrarDialogoChecklist = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Checklist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { mostrarDialogoFeedback = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Feedback", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(onClick = { mostrarDialogoIncidencia = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Incidencia", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

fun abrirCamaraParaIncidencia(
    context: android.content.Context,
    solicitud: SolicitudEntity,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onUriCreated: (Uri) -> Unit
) {
    try {
        val photoFile = File.createTempFile("evidencia_${solicitud.id}_", ".jpg", context.filesDir)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        onUriCreated(uri)
        cameraLauncher.launch(uri)
    } catch (e: Exception) {
        Toast.makeText(context, "Error al abrir cámara: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun FilaDetalle(icono: ImageVector, texto: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icono, contentDescription = null, tint = color.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = texto, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
fun VistaUsuarios(lista: List<UsuarioEntity>) {
    var filtroRol by remember { mutableStateOf<RolUsuario?>(null) }
    var textoBusqueda by remember { mutableStateOf("") }

    val usuariosFiltrados = remember(lista, filtroRol, textoBusqueda) {
        lista.filter { usuario ->
            (filtroRol == null || usuario.rol == filtroRol) &&
            (textoBusqueda.isBlank() ||
             usuario.nombre.contains(textoBusqueda, ignoreCase = true) ||
             usuario.email.contains(textoBusqueda, ignoreCase = true))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar usuario por nombre o correo...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (textoBusqueda.isNotBlank()) {
                {
                    IconButton(onClick = { textoBusqueda = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = filtroRol == null,
                onClick = { filtroRol = null },
                label = { Text("Todos (${lista.size})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filtroRol == RolUsuario.CLIENTE,
                onClick = { filtroRol = if (filtroRol == RolUsuario.CLIENTE) null else RolUsuario.CLIENTE },
                label = { Text("Clientes (${lista.count { it.rol == RolUsuario.CLIENTE }})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filtroRol == RolUsuario.TRABAJADOR,
                onClick = { filtroRol = if (filtroRol == RolUsuario.TRABAJADOR) null else RolUsuario.TRABAJADOR },
                label = { Text("Personal (${lista.count { it.rol == RolUsuario.TRABAJADOR }})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filtroRol == RolUsuario.ADMINISTRADOR,
                onClick = { filtroRol = if (filtroRol == RolUsuario.ADMINISTRADOR) null else RolUsuario.ADMINISTRADOR },
                label = { Text("Admins (${lista.count { it.rol == RolUsuario.ADMINISTRADOR }})", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (usuariosFiltrados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontraron usuarios", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(usuariosFiltrados) { usuario ->
                    TarjetaPerfilUsuario(usuario)
                }
            }
        }
    }
}

@Composable
fun TarjetaPerfilUsuario(usuario: UsuarioEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!usuario.fotoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(Uri.parse(usuario.fotoUri))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = usuario.nombre.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = usuario.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = usuario.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(8.dp))

            InsigniaRolPerfilSmall(rol = usuario.rol)

            Spacer(modifier = Modifier.height(8.dp))

            if (!usuario.telefono.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = usuario.telefono, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (!usuario.direccion.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = usuario.direccion, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (!usuario.cargoEspecialidad.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = usuario.cargoEspecialidad, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun InsigniaRolPerfilSmall(rol: RolUsuario) {
    val (etiqueta, colorFondo, colorTexto) = when (rol) {
        RolUsuario.ADMINISTRADOR -> Triple("Administrador", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        RolUsuario.TRABAJADOR -> Triple("Personal / Garzón", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        RolUsuario.CLIENTE -> Triple("Cliente", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
    }

    Surface(shape = RoundedCornerShape(8.dp), color = colorFondo) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = colorTexto,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}