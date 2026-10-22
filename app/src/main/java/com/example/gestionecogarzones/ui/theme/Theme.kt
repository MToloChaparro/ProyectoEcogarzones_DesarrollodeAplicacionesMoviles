package com.example.gestionecogarzones.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = VerdeEcogarzones,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = VerdeOliva,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,

    secondary = VerdeOliva,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Arena,
    onSecondaryContainer = VerdeEcogarzones,

    tertiary = Arena,
    onTertiary = VerdeEcogarzones,

    background = Crema,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),

    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = Arena.copy(alpha = 0.5f),
    onSurfaceVariant = VerdeEcogarzones
)
private val DarkColors = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    primaryContainer = VerdeEcogarzones,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,

    secondary = VerdeOlivaClaro,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    secondaryContainer = VerdeOliva,
    onSecondaryContainer = androidx.compose.ui.graphics.Color.White,

    background = FondoOscuro,
    onBackground = androidx.compose.ui.graphics.Color.White,

    surface = androidx.compose.ui.graphics.Color(0xFF2B2A2E),
    onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun GestionEcogarzonesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}