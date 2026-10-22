package com.example.gestionecogarzones.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CampoTextoValidado(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    mensajeError: String? = null,
    esNumero: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        isError = mensajeError != null,
        keyboardOptions = if (esNumero) {
            androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        } else {
            androidx.compose.foundation.text.KeyboardOptions.Default
        },
        modifier = modifier.fillMaxWidth()
    )

    AnimatedVisibility(visible = mensajeError != null) {
        Text(
            text = mensajeError ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}