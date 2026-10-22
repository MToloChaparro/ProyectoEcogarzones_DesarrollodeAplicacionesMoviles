package com.example.gestionecogarzones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestionecogarzones.ui.components.CampoTextoValidado
import com.example.gestionecogarzones.ui.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var esAdmin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido a",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Ecogarzones",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = !esAdmin,
                onClick = { esAdmin = false },
                label = { Text("Soy Cliente") }
            )
            FilterChip(
                selected = esAdmin,
                onClick = { esAdmin = true },
                label = { Text("Soy Administrador") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        CampoTextoValidado(
            valor = email,
            onValorCambia = { email = it },
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    if (esAdmin) {
                        navController.navigate(Rutas.PanelAdmin.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Rutas.Inicio.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Ingresar")
        }
    }
}