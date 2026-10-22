package com.example.gestionecogarzones.ui.screens.cliente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import com.example.gestionecogarzones.ui.components.CampoTextoValidado
import com.example.gestionecogarzones.ui.navigation.Rutas
import com.example.gestionecogarzones.viewmodel.SolicitudViewModel
import com.example.gestionecogarzones.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioClienteScreen(
    navController: NavController,
    repository: SolicitudRepository
) {
    val viewModel: SolicitudViewModel = viewModel(factory = ViewModelFactory(repository))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.mostrarResumen) "Confirmar Evento" else "Cotizar Evento", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.mostrarResumen) viewModel.volverAEditar() else navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.mostrarResumen) {
                PantallaResumen(
                    viewModel = viewModel,
                    onEditar = { viewModel.volverAEditar() },
                    onConfirmar = {
                        viewModel.guardarSolicitud {
                            navController.navigate(Rutas.ConfirmacionExito.ruta) {
                                popUpTo(Rutas.FormularioCliente.ruta) { inclusive = true }
                            }
                        }
                    }
                )
            } else {
                PantallaFormulario(
                    viewModel = viewModel,
                    onSiguiente = { viewModel.irAResumen() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(viewModel: SolicitudViewModel, onSiguiente: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("1. Datos de Contacto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        CampoTextoValidado(valor = viewModel.nombreCliente, onValorCambia = viewModel::onNombreChange, etiqueta = "Nombre completo", icono = Icons.Default.Person, mensajeError = viewModel.nombreError)
        CampoTextoValidado(valor = viewModel.telefono, onValorCambia = viewModel::onTelefonoChange, etiqueta = "Teléfono", icono = Icons.Default.Phone, mensajeError = viewModel.telefonoError, esNumero = true)

        Spacer(modifier = Modifier.height(8.dp))

        Text("2. Detalles del Evento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        var expandirServicio by remember { mutableStateOf(false) }
        val opcionesServicio = listOf("Banquetería Completa", "Cóctel / Coffee Break", "Solo Barra")

        ExposedDropdownMenuBox(expanded = expandirServicio, onExpandedChange = { expandirServicio = it }) {
            OutlinedTextField(
                value = viewModel.tipoServicio,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Servicio General") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirServicio) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = expandirServicio, onDismissRequest = { expandirServicio = false }) {
                opcionesServicio.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            viewModel.onTipoServicioChange(opcion)
                            expandirServicio = false
                        }
                    )
                }
            }
        }

        CampoTextoValidado(valor = viewModel.tipoEvento, onValorCambia = viewModel::onTipoEventoChange, etiqueta = "Tipo de evento (Boda, etc.)", icono = Icons.Default.Celebration)

        var mostrarCalendario by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            CampoTextoValidado(
                valor = viewModel.fechaEvento,
                onValorCambia = {},
                etiqueta = "Fecha (Mín. 15 días)",
                icono = Icons.Default.CalendarMonth,
                mensajeError = viewModel.fechaError
            )
            Box(modifier = Modifier.matchParentSize().clickable { mostrarCalendario = true })
        }

        if (mostrarCalendario) {
            val datePickerState = rememberDatePickerState(
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= viewModel.fechaMinima
                    }
                }
            )
            DatePickerDialog(
                onDismissRequest = { mostrarCalendario = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.onFechaSeleccionada(it) }
                        mostrarCalendario = false
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        CampoTextoValidado(valor = viewModel.direccion, onValorCambia = viewModel::onDireccionChange, etiqueta = "Dirección", icono = Icons.Default.LocationOn, mensajeError = viewModel.direccionError)
        CampoTextoValidado(valor = viewModel.cantidadInvitados, onValorCambia = viewModel::onInvitadosChange, etiqueta = "Invitados", icono = Icons.Default.Groups, mensajeError = viewModel.invitadosError, esNumero = true)

        if (viewModel.cantidadInvitados.isNotBlank() && viewModel.invitadosError == null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Sugerencia de personal:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    FilaSugerencia(icono = Icons.Default.Restaurant, texto = "${viewModel.garzonesSugeridos} Garzones")
                    FilaSugerencia(icono = Icons.Default.SoupKitchen, texto = "${viewModel.cocinerosSugeridos} Cocinero(s)")
                    FilaSugerencia(icono = Icons.Default.LocalBar, texto = "${viewModel.bartendersSugeridos} Bartender(s)")
                    FilaSugerencia(icono = Icons.Default.LocalDining, texto = "${viewModel.coperosSugeridos} Copero(s)")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("3. Menús y Barras", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        val totalInvitados = viewModel.cantidadInvitados.toIntOrNull() ?: 0
        val totalMenus = viewModel.menusSeleccionados.values.sum()
        val totalBarras = viewModel.barrasSeleccionadas.values.sum()

        if (totalInvitados > 0) {

            if (viewModel.tipoServicio != "Solo Barra") {
                Text("Distribuye los $totalInvitados platos entre las opciones:", style = MaterialTheme.typography.bodySmall)

                viewModel.catalogoMenusActual.keys.forEach { menu ->
                    val cant = viewModel.menusSeleccionados[menu] ?: 0
                    TarjetaMenu(titulo = menu, cantidad = cant, maxAlcanzado = totalMenus >= totalInvitados, detalles = viewModel.catalogoMenusActual[menu] ?: emptyList(), onCantidadCambia = { viewModel.actualizarCantidadMenu(menu, it) })
                }

                if (viewModel.errorMenu != null) {
                    Text(viewModel.errorMenu!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("Menús asignados: $totalMenus / $totalInvitados", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Distribuye las $totalInvitados atenciones de barra:", style = MaterialTheme.typography.bodySmall)

            viewModel.catalogoBarrasActual.keys.forEach { barra ->
                val cant = viewModel.barrasSeleccionadas[barra] ?: 0
                TarjetaMenu(titulo = barra, cantidad = cant, maxAlcanzado = totalBarras >= totalInvitados, detalles = viewModel.catalogoBarrasActual[barra] ?: emptyList(), onCantidadCambia = { viewModel.actualizarCantidadBarra(barra, it) })
            }

            if (viewModel.errorBarra != null) {
                Text(viewModel.errorBarra!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Barras asignadas: $totalBarras / $totalInvitados", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Text("Ingresa la cantidad de invitados para habilitar los menús.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("4. Logística y Términos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        var expandirInsumos by remember { mutableStateOf(false) }
        val opcionesInsumos = listOf("Ecogarzones provee todo", "Yo llevo solo vajilla", "Yo llevo solo ingredientes", "Yo llevo insumos completos")

        ExposedDropdownMenuBox(expanded = expandirInsumos, onExpandedChange = { expandirInsumos = it }) {
            OutlinedTextField(
                value = viewModel.tipoInsumos.ifBlank { "Provisión de Insumos" },
                onValueChange = {}, readOnly = true,
                label = { Text("Provisión de Insumos") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirInsumos) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = expandirInsumos, onDismissRequest = { expandirInsumos = false }) {
                opcionesInsumos.forEach { opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = { viewModel.onTipoInsumosChange(opcion); expandirInsumos = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = viewModel.aceptaTerminos, onCheckedChange = viewModel::onTerminosChange)
            Text("Acepto los términos legales del servicio.", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSiguiente,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = viewModel.formularioEsValido() && viewModel.aceptaTerminos && !viewModel.isGuardando,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Revisar y Enviar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PantallaResumen(viewModel: SolicitudViewModel, onEditar: () -> Unit, onConfirmar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Revisa los detalles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FilaResumen("Cliente", viewModel.nombreCliente)
                FilaResumen("Teléfono", viewModel.telefono)
                FilaResumen("Servicio", viewModel.tipoServicio)
                FilaResumen("Evento", viewModel.tipoEvento)
                FilaResumen("Fecha", viewModel.fechaEvento)
                FilaResumen("Dirección", viewModel.direccion)
                FilaResumen("Invitados", viewModel.cantidadInvitados)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                if (viewModel.tipoServicio != "Solo Barra") {
                    Text("Menús pedidos:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    if (viewModel.menusSeleccionados.isEmpty()) {
                        Text("Ninguno", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    } else {
                        viewModel.menusSeleccionados.forEach { (menu, cant) -> FilaResumen(menu, cant.toString()) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Barras pedidas:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                if (viewModel.barrasSeleccionadas.isEmpty()) {
                    Text("Ninguna", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                } else {
                    viewModel.barrasSeleccionadas.forEach { (barra, cant) -> FilaResumen(barra, cant.toString()) }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onConfirmar, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), enabled = !viewModel.isGuardando) {
            if (viewModel.isGuardando) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar Solicitud", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        OutlinedButton(onClick = onEditar, modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 12.dp), shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Datos", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun FilaResumen(titulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(titulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TarjetaMenu(titulo: String, cantidad: Int, maxAlcanzado: Boolean, detalles: List<String>, onCantidadCambia: (Int) -> Unit) {
    var mostrarInfo by remember { mutableStateOf(false) }
    var textoCantidad by remember { mutableStateOf(if (cantidad == 0) "" else cantidad.toString()) }

    LaunchedEffect(cantidad) {
        if (textoCantidad != cantidad.toString()) {
            textoCantidad = if (cantidad == 0) "" else cantidad.toString()
        }
    }

    if (mostrarInfo) {
        AlertDialog(
            onDismissRequest = { mostrarInfo = false },
            title = { Text("Menú: $titulo") },
            text = {
                Column {
                    Text("El menú incluye:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    detalles.forEach { item -> Text("• $item", style = MaterialTheme.typography.bodyMedium) }
                }
            },
            confirmButton = { TextButton(onClick = { mostrarInfo = false }) { Text("Cerrar") } }
        )
    }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = { mostrarInfo = true }) { Icon(imageVector = Icons.Default.Info, contentDescription = "Ver menú", tint = MaterialTheme.colorScheme.primary) }
            }
            Text(text = detalles.joinToString(" | "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(bottom = 12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                FilledTonalIconButton(onClick = { if (cantidad > 0) onCantidadCambia(cantidad - 1) }, enabled = cantidad > 0, modifier = Modifier.size(44.dp)) { Icon(Icons.Default.Remove, contentDescription = "Restar") }
                OutlinedTextField(
                    value = textoCantidad,
                    onValueChange = { nuevoTexto -> if (nuevoTexto.all { it.isDigit() }) { textoCantidad = nuevoTexto; onCantidadCambia(nuevoTexto.toIntOrNull() ?: 0) } },
                    modifier = Modifier.width(90.dp).height(52.dp),
                    textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedBorderColor = Color.Transparent)
                )
                FilledTonalIconButton(onClick = { if (!maxAlcanzado) onCantidadCambia(cantidad + 1) }, enabled = !maxAlcanzado, modifier = Modifier.size(44.dp)) { Icon(Icons.Default.Add, contentDescription = "Sumar") }
            }
        }
    }
}

@Composable
fun FilaSugerencia(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(imageVector = icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = texto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}