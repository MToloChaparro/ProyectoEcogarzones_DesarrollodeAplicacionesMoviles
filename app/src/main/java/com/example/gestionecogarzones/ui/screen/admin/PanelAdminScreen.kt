package com.example.gestionecogarzones.ui.screens.admin

import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.EventNote
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gestionecogarzones.data.local.entity.EstadoEvento
import com.example.gestionecogarzones.data.local.entity.EstadoIncidencia
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import com.example.gestionecogarzones.ui.navigation.Rutas
import com.example.gestionecogarzones.viewmodel.AdminViewModel
import com.example.gestionecogarzones.viewmodel.ViewModelFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelAdminScreen(
    navController: NavController,
    repository: SolicitudRepository
) {
    val viewModel: AdminViewModel = viewModel(factory = ViewModelFactory(repository))

    val listaSolicitudes by viewModel.solicitudes.collectAsState()
    val listaIncidencias by viewModel.incidencias.collectAsState()
    val listaInventario by viewModel.inventario.collectAsState()

    var pestanaSeleccionada by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
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
            }

            when (pestanaSeleccionada) {
                0 -> VistaResumen(viewModel)
                1 -> VistaEventos(listaSolicitudes, viewModel)
                2 -> VistaIncidencias(listaIncidencias, viewModel)
                3 -> VistaInventario(listaInventario, viewModel)
            }
        }
    }
}

@Composable
fun VistaResumen(viewModel: AdminViewModel) {
    val totalEventos by viewModel.totalEventos.collectAsState(initial = 0)
    val eventosPendientes by viewModel.eventosPendientes.collectAsState(initial = 0)
    val totalInvitados by viewModel.totalInvitados.collectAsState(initial = 0)
    val valorInventario by viewModel.valorTotalInventario.collectAsState(initial = 0.0)

    val eventosActivos by viewModel.eventosActivos.collectAsState(initial = 0)
    val incidenciasPendientes by viewModel.incidenciasPendientes.collectAsState(initial = 0)
    val invitadosConfirmados by viewModel.invitadosConfirmados.collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Panel de Control",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text("Eventos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaMetrica("Total", totalEventos.toString(), Icons.AutoMirrored.Filled.EventNote, MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
            TarjetaMetrica("Pendientes", eventosPendientes.toString(), Icons.Default.HourglassEmpty, Color(0xFFFFF3E0), Modifier.weight(1f))
            TarjetaMetrica("Activos", eventosActivos.toString(), Icons.Default.PlayCircle, Color(0xFFE3F2FD), Modifier.weight(1f))
        }

        Text("Operación", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaMetrica("Invitados Totales", totalInvitados.toString(), Icons.Default.Groups, MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
            TarjetaMetrica("Confirmados", invitadosConfirmados.toString(), Icons.Default.HowToReg, Color(0xFFE8F5E9), Modifier.weight(1f))
        }

        Text("Alertas y Finanzas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            val colorIncidencia = if (incidenciasPendientes > 0) Color(0xFFFFEBEE) else Color(0xFFF5F5F5)
            val colorTextoIncidencia = if (incidenciasPendientes > 0) Color(0xFFC62828) else Color.Black

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorIncidencia)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = colorTextoIncidencia)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(incidenciasPendientes.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = colorTextoIncidencia)
                    Text("Incidencias", style = MaterialTheme.typography.bodySmall, color = colorTextoIncidencia.copy(alpha = 0.8f))
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$${String.format(java.util.Locale.getDefault(), "%,.0f", valorInventario)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text("Valor Inventario", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    if (incidenciasPendientes > 0) "Atención Requerida" else "¡Todo bajo control!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (incidenciasPendientes > 0) "Tienes $incidenciasPendientes incidencias sin resolver que requieren tu atención." else "No tienes incidencias pendientes. Tienes $eventosPendientes solicitudes esperando confirmación.",
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
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
fun VistaInventario(lista: List<InventarioEntity>, viewModel: AdminViewModel) {
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    var itemAEditar by remember { mutableStateOf<InventarioEntity?>(null) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val valorTotal by viewModel.valorTotalInventario.collectAsState(initial = 0.0)

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("¿Confirmar cambios?") },
            text = { Text("Se actualizarán los datos del insumo en el inventario. Esta acción quedará registrada.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    itemAEditar?.let { viewModel.actualizarItemCompleto(it) }
                    itemAEditar = null
                }) { Text("Sí, guardar") }
            },
            dismissButton = { TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") } }
        )
    }

    if (itemAEditar != null) {
        val item = itemAEditar!!
        var editCantidad by remember { mutableStateOf(item.cantidad.toString()) }
        var editEstado by remember { mutableStateOf(item.estado) }
        var editFecha by remember { mutableStateOf(item.fechaAdquisicion) }

        var expandirEstado by remember { mutableStateOf(false) }
        var mostrarCalendario by remember { mutableStateOf(false) }

        val estadosDisponibles = listOf("Disponible", "En uso", "Dañado", "Vencido", "Defectuoso", "En mantenimiento")

        AlertDialog(
            onDismissRequest = { itemAEditar = null },
            title = { Text("Editar: ${item.nombreItem}") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = editCantidad, onValueChange = { if (it.all { c -> c.isDigit() }) editCantidad = it }, label = { Text("Cantidad exacta") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(expanded = expandirEstado, onExpandedChange = { expandirEstado = it }) {
                        OutlinedTextField(value = editEstado, onValueChange = {}, readOnly = true, label = { Text("Estado del insumo") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirEstado) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth())
                        ExposedDropdownMenu(expanded = expandirEstado, onDismissRequest = { expandirEstado = false }) {
                            estadosDisponibles.forEach { est -> DropdownMenuItem(text = { Text(est) }, onClick = { editEstado = est; expandirEstado = false }) }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(value = editFecha.ifBlank { "Seleccionar fecha" }, onValueChange = {}, readOnly = true, label = { Text("Fecha de adquisición") }, trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }, modifier = Modifier.fillMaxWidth())
                        Box(modifier = Modifier.matchParentSize().clickable { mostrarCalendario = true })
                    }

                    if (mostrarCalendario) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { mostrarCalendario = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                        editFecha = sdf.format(java.util.Date(it))
                                    }
                                    mostrarCalendario = false
                                }) { Text("Aceptar") }
                            },
                            dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") } }
                        ) { DatePicker(state = datePickerState) }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    itemAEditar = item.copy(cantidad = editCantidad.toIntOrNull() ?: item.cantidad, estado = editEstado, fechaAdquisicion = editFecha)
                    mostrarConfirmacion = true
                }) { Text("Aplicar cambios") }
            },
            dismissButton = { TextButton(onClick = { itemAEditar = null }) { Text("Cancelar") } }
        )
    }

    if (mostrarDialogoAgregar) {
        var nombre by remember { mutableStateOf("") }
        var cantidad by remember { mutableStateOf("") }
        var costo by remember { mutableStateOf("") }
        var proveedor by remember { mutableStateOf("") }
        var categoria by remember { mutableStateOf("Vajilla") }
        var expandirCategoria by remember { mutableStateOf(false) }
        var unidad by remember { mutableStateOf("Unidades") }
        var expandirUnidad by remember { mutableStateOf(false) }

        val categoriasDisponibles = listOf("Vajilla", "Cristalería", "Mobiliario", "Textiles", "Cocina", "Bebidas", "Alimentos", "Decoración", "Aseo")
        val unidadesDisponibles = listOf("Unidades", "Kilos", "Gramos", "Litros", "Mililitros", "Cajas", "Paquetes")

        AlertDialog(
            onDismissRequest = { mostrarDialogoAgregar = false },
            title = { Text("Agregar al Inventario") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del ítem") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(expanded = expandirCategoria, onExpandedChange = { expandirCategoria = it }) {
                        OutlinedTextField(value = categoria, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirCategoria) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth())
                        ExposedDropdownMenu(expanded = expandirCategoria, onDismissRequest = { expandirCategoria = false }) {
                            categoriasDisponibles.forEach { cat -> DropdownMenuItem(text = { Text(cat) }, onClick = { categoria = cat; expandirCategoria = false }) }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(expanded = expandirUnidad, onExpandedChange = { expandirUnidad = it }) {
                        OutlinedTextField(value = unidad, onValueChange = {}, readOnly = true, label = { Text("Formato de Unidad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirUnidad) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth())
                        ExposedDropdownMenu(expanded = expandirUnidad, onDismissRequest = { expandirUnidad = false }) {
                            unidadesDisponibles.forEach { uni -> DropdownMenuItem(text = { Text(uni) }, onClick = { unidad = uni; expandirUnidad = false }) }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = cantidad, onValueChange = { if (it.all { char -> char.isDigit() }) cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = costo, onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) costo = it }, label = { Text("Costo por unidad") }, prefix = { Text("$ ") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = proveedor, onValueChange = { proveedor = it }, label = { Text("Proveedor (opcional)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && cantidad.isNotBlank()) {
                            viewModel.agregarItemInventario(nombre, categoria, cantidad.toIntOrNull() ?: 0, costo.toDoubleOrNull() ?: 0.0, proveedor, unidad)
                            mostrarDialogoAgregar = false
                        }
                    },
                    enabled = nombre.isNotBlank() && cantidad.isNotBlank()
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoAgregar = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Valor Total del Inventario", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(text = "$${String.format(Locale.getDefault(), "%,.0f", valorTotal)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (lista.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("El inventario está vacío", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(lista) { item ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.nombreItem, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("${item.categoria} (${item.unidad})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Button(onClick = { itemAEditar = item }, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Editar", fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Stock: ${item.cantidad}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("Estado: ${item.estado}", style = MaterialTheme.typography.bodySmall, color = if(item.estado == "Disponible") Color(0xFF4CAF50) else Color.Red)
                                }
                                if (item.fechaAdquisicion.isNotBlank()) { Text("Fecha adq: ${item.fechaAdquisicion}", style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Costo unitario: $${String.format(Locale.getDefault(), "%,.0f", item.costoUnidad)}", style = MaterialTheme.typography.bodySmall)
                                    Text("Total: $${String.format(Locale.getDefault(), "%,.0f", item.valorTotal)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(onClick = { mostrarDialogoAgregar = true }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ítem", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
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
    var textoIncidencia by remember { mutableStateOf("") }
    var tipoIncidencia by remember { mutableStateOf("Logística") }
    var expandirMenuTipo by remember { mutableStateOf(false) }
    var fotoAdjuntaUri by remember { mutableStateOf<Uri?>(null) }
    var menuEstadoExpandido by remember { mutableStateOf(false) }

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

    if (mostrarDialogoIncidencia) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoIncidencia = false; fotoAdjuntaUri = null; textoIncidencia = "" },
            title = { Text("Reportar Incidencia", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    ExposedDropdownMenuBox(expanded = expandirMenuTipo, onExpandedChange = { expandirMenuTipo = it }) {
                        OutlinedTextField(value = tipoIncidencia, onValueChange = {}, readOnly = true, label = { Text("Tipo de incidencia") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMenuTipo) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = RoundedCornerShape(12.dp))
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
            Spacer(modifier = Modifier.height(10.dp))
            FilaDetalle(icono = Icons.Default.CalendarMonth, texto = solicitud.fechaEvento)
            Spacer(modifier = Modifier.height(10.dp))
            FilaDetalle(icono = Icons.Default.Groups, texto = "${solicitud.cantidadInvitados} invitados")
            Spacer(modifier = Modifier.height(10.dp))
            FilaDetalle(icono = Icons.Default.LocationOn, texto = solicitud.direccion)

            if (solicitud.menusSolicitados.isNotBlank() && solicitud.menusSolicitados != "Sin menús") { Spacer(modifier = Modifier.height(10.dp)); FilaDetalle(icono = Icons.Default.RestaurantMenu, texto = "Menús: ${solicitud.menusSolicitados}") }
            if (solicitud.barrasSolicitadas.isNotBlank() && solicitud.barrasSolicitadas != "Sin barras") { Spacer(modifier = Modifier.height(10.dp)); FilaDetalle(icono = Icons.Default.LocalBar, texto = "Barras: ${solicitud.barrasSolicitadas}") }
            if (solicitud.detalleInsumos.isNotBlank() && solicitud.detalleInsumos != "Ecogarzones provee todo") {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Insumos: ${solicitud.detalleInsumos}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            if (solicitud.restriccionesAlimentarias.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(8.dp)) { FilaDetalle(icono = Icons.Default.Warning, texto = solicitud.restriccionesAlimentarias, color = MaterialTheme.colorScheme.error) }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(12.dp)) {
                Column {
                    Text("Gestión en Terreno", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { mostrarDialogoIncidencia = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(6.dp)); Text("Incidencia", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(onClick = { if (hasCameraPermission) { abrirCamaraParaIncidencia(context, solicitud, cameraLauncher) { uri -> fotoAdjuntaUri = uri; mostrarDialogoIncidencia = true } } else { permissionLauncher.launch(Manifest.permission.CAMERA) } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(6.dp)); Text("Evidencia", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
        Icon(imageVector = icono, contentDescription = null, tint = color.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = texto, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}