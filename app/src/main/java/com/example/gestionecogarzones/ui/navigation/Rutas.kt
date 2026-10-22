package com.example.gestionecogarzones.ui.navigation

sealed class Rutas(val ruta: String) {
    object Login : Rutas("login")
    object Inicio : Rutas("inicio")
    object Registro : Rutas("registro")
    object FormularioCliente : Rutas("formulario_cliente")
    object PanelAdmin : Rutas("panel_admin")
    object ReporteCliente : Rutas("reporte_cliente")

    object ConfirmacionExito : Rutas("confirmacion_exito")
}