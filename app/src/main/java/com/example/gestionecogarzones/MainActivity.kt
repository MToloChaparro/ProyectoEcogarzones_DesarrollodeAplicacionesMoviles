package com.example.gestionecogarzones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionecogarzones.data.local.database.AppBD
import com.example.gestionecogarzones.data.repository.SolicitudRepository
import com.example.gestionecogarzones.ui.navigation.Rutas
import com.example.gestionecogarzones.ui.screens.LoginScreen
import com.example.gestionecogarzones.ui.screens.PantallaInicio
import com.example.gestionecogarzones.ui.screens.admin.PanelAdminScreen
import com.example.gestionecogarzones.ui.screens.cliente.ConfirmacionScreen
import com.example.gestionecogarzones.ui.screens.cliente.FormularioClienteScreen
import com.example.gestionecogarzones.ui.screens.cliente.ReporteClienteScreen
import com.example.gestionecogarzones.ui.theme.GestionEcogarzonesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppBD.getDatabase(this)

        val repository = SolicitudRepository(
            database.solicitudDao(),
            database.incidenciaDao(),
            database.inventarioDao()
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (repository.contarInventario() == 0) {
                repository.inicializarInventario()
            }
        }

        setContent {
            GestionEcogarzonesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(repository)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(repository: SolicitudRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.Login.ruta) {
        composable(Rutas.Login.ruta) { LoginScreen(navController) }
        composable(Rutas.Inicio.ruta) { PantallaInicio(navController) }
        composable(Rutas.FormularioCliente.ruta) { FormularioClienteScreen(navController, repository) }
        composable(Rutas.PanelAdmin.ruta) { PanelAdminScreen(navController, repository) }
        composable(Rutas.ReporteCliente.ruta) { ReporteClienteScreen(navController, repository) }
        composable(Rutas.ConfirmacionExito.ruta) { ConfirmacionScreen(navController) }
    }
}