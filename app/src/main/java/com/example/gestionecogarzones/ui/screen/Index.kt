package com.example.gestionecogarzones.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionecogarzones.ui.navigation.Rutas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(navController: NavController) {
    var contenidoVisible by remember { mutableStateOf(false) }
    var mostrarDialogoSalir by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        contenidoVisible = true
    }

    if (mostrarDialogoSalir) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSalir = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoSalir = false
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ecogarzones",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { mostrarDialogoSalir = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        AnimatedVisibility(
            visible = contenidoVisible,
            enter = fadeIn(animationSpec = tween(800)) +
                    slideInVertically(animationSpec = tween(800), initialOffsetY = { it / 2 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                var escalarIcono by remember { mutableStateOf(false) }
                val escala by animateFloatAsState(
                    targetValue = if (escalarIcono) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
                    label = "escalaIcono"
                )

                LaunchedEffect(Unit) {
                    escalarIcono = true
                    delay(300)
                    escalarIcono = false
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(escala)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Hola de nuevo!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "¿Qué te gustaría hacer hoy?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                TarjetaAccionRapida(
                    icono = Icons.Default.Event,
                    titulo = "Solicitar un Evento",
                    descripcion = "Cotiza nuestros servicios de banquetería y garzones.",
                    colorIcono = MaterialTheme.colorScheme.primary,
                    colorFondo = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    onClick = { navController.navigate(Rutas.FormularioCliente.ruta) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                TarjetaAccionRapida(
                    icono = Icons.Default.Report,
                    titulo = "Reportar un Problema",
                    descripcion = "Infórmanos sobre cualquier incidencia en tu evento.",
                    colorIcono = MaterialTheme.colorScheme.error,
                    colorFondo = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    onClick = { navController.navigate(Rutas.ReporteCliente.ruta) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "¿Cómo funciona?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        FilaPasoInteractiva(
                            icono = Icons.Default.CalendarMonth,
                            titulo = "1. Elige la fecha",
                            descripcion = "Dinos cuándo será tu evento.",
                            scope = scope
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FilaPasoInteractiva(
                            icono = Icons.Default.Restaurant,
                            titulo = "2. Selecciona el servicio",
                            descripcion = "Cuéntanos qué necesitas.",
                            scope = scope
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FilaPasoInteractiva(
                            icono = Icons.Default.Star,
                            titulo = "3. ¡Disfruta!",
                            descripcion = "Nosotros nos encargamos del resto.",
                            scope = scope
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Síguenos o contáctanos",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    BotonRedSocial(
                        icono = Icons.Default.Phone,
                        nombre = "WhatsApp",
                        colorFondo = MaterialTheme.colorScheme.tertiaryContainer,
                        colorIcono = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        val numeroWhatsApp = "56977480467"
                        val url = "https://wa.me/$numeroWhatsApp"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                    }

                    BotonRedSocial(
                        icono = Icons.Default.Share,
                        nombre = "Instagram",
                        colorFondo = MaterialTheme.colorScheme.secondaryContainer,
                        colorIcono = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ecogarzones/"))
                        context.startActivity(intent)
                    }

                    BotonRedSocial(
                        icono = Icons.Default.Email,
                        nombre = "Correo",
                        colorFondo = MaterialTheme.colorScheme.primaryContainer,
                        colorIcono = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ecogarzones@gmail.com"))
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaAccionRapida(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    colorIcono: Color,
    colorFondo: Color,
    onClick: () -> Unit
) {
    var presionado by remember { mutableStateOf(false) }
    val escala by animateFloatAsState(targetValue = if (presionado) 0.97f else 1f, label = "escalaAccion")

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(escala)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                presionado = true
                scope.launch {
                    delay(150)
                    presionado = false
                    onClick()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FilaPasoInteractiva(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    scope: kotlinx.coroutines.CoroutineScope
) {
    var presionado by remember { mutableStateOf(false) }
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.95f else 1f,
        label = "escalaPaso"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .scale(escala)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                presionado = true
                scope.launch {
                    delay(150)
                    presionado = false
                }
            }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BotonRedSocial(
    icono: ImageVector,
    nombre: String,
    colorFondo: Color,
    colorIcono: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(colorFondo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = nombre,
                tint = colorIcono,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = nombre,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}