package edu.ucne.credifast.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(val titulo: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    CLIENTES("Clientes", Icons.Filled.People),
    PRESTAMOS("Préstamos", Icons.Filled.AttachMoney),
    COBROS("Cobros", Icons.Filled.CalendarMonth),
    MAS("Más", Icons.Filled.Menu)
}