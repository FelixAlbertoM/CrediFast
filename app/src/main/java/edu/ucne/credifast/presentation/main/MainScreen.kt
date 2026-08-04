package edu.ucne.credifast.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import edu.ucne.credifast.presentation.cliente.list.ClienteListScreen
import edu.ucne.credifast.presentation.cobro.list.CobrosScreen
import edu.ucne.credifast.presentation.prestamo.list.PrestamoListScreen

@Composable
fun MainScreen(
    onAgregarCliente: () -> Unit,
    onClienteClick: (Int) -> Unit,
    onOtorgarPrestamo: () -> Unit,
    onPrestamoClick: (Int) -> Unit,
    onCobrarCuota: (Int) -> Unit
) {
    var tabActual by remember { mutableStateOf(MainTab.CLIENTES) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tabActual == tab,
                        onClick = { tabActual = tab },
                        icon = { Icon(tab.icono, contentDescription = tab.titulo) },
                        label = { Text(tab.titulo) }
                    )
                }
            }
        },
        floatingActionButton = {
            when (tabActual) {
                MainTab.CLIENTES -> FloatingActionButton(onClick = onAgregarCliente) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar cliente")
                }
                MainTab.PRESTAMOS -> FloatingActionButton(onClick = onOtorgarPrestamo) {
                    Icon(Icons.Filled.Add, contentDescription = "Otorgar préstamo")
                }
                MainTab.COBROS -> {}
            }
        }
    ) { padding ->
        when (tabActual) {
            MainTab.CLIENTES -> ClienteListScreen(
                modifier = Modifier.padding(padding),
                onClienteClick = onClienteClick
            )
            MainTab.PRESTAMOS -> PrestamoListScreen(
                modifier = Modifier.padding(padding),
                onPrestamoClick = onPrestamoClick
            )
            MainTab.COBROS -> CobrosScreen(
                modifier = Modifier.padding(padding),
                onCobrarCuota = onCobrarCuota
            )
        }
    }
}