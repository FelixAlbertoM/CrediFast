package edu.ucne.credifast.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import edu.ucne.credifast.presentation.auth.LoginScreen
import edu.ucne.credifast.presentation.cliente.edit.ClienteEditScreen
import edu.ucne.credifast.presentation.cliente.list.ClienteListScreen
import edu.ucne.credifast.presentation.cobro.list.CobrosScreen
import edu.ucne.credifast.presentation.cobro.pago.CobroPagoScreen
import edu.ucne.credifast.presentation.cobro.recibo.ReciboScreen
import edu.ucne.credifast.presentation.prestamo.detail.PrestamoDetailScreen
import edu.ucne.credifast.presentation.prestamo.edit.PrestamoEditScreen
import edu.ucne.credifast.presentation.prestamo.list.PrestamoListScreen

@Composable
fun CrediFastNavHost(isLoggedIn: Boolean) {
    val backStack = rememberNavBackStack(
        if (isLoggedIn) Screen.ClienteList else Screen.Login
    )

    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
        entryProvider = entryProvider {

            entry<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(Screen.ClienteList)
                    }
                )
            }

            entry<Screen.ClienteList> {
                ClienteListScreen(
                    onAgregarCliente = { backStack.add(Screen.ClienteEdit(0)) },
                    onClienteClick = { id -> backStack.add(Screen.ClienteEdit(id)) },
                    onIrAPrestamos = {
                        if (backStack.lastOrNull() != Screen.PrestamoList) {
                            backStack.clear()
                            backStack.add(Screen.PrestamoList)
                        }
                    }
                )
            }

            entry<Screen.ClienteEdit> { key ->
                ClienteEditScreen(
                    clienteId = key.clienteId,
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) }
                )
            }

            entry<Screen.PrestamoList> {
                PrestamoListScreen(
                    onOtorgarPrestamo = { backStack.add(Screen.PrestamoEdit) },
                    onPrestamoClick = { id -> backStack.add(Screen.PrestamoDetail(id)) },
                    onIrAClientes = {
                        if (backStack.lastOrNull() != Screen.ClienteList) {
                            backStack.clear()
                            backStack.add(Screen.ClienteList)
                        }
                    },
                    onIrACobros = {
                        if (backStack.lastOrNull() != Screen.CobrosList) {
                            backStack.clear()
                            backStack.add(Screen.CobrosList)
                        }
                    }
                )
            }

            entry<Screen.PrestamoEdit> {
                PrestamoEditScreen(
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) }
                )
            }

            entry<Screen.PrestamoDetail> { key ->
                PrestamoDetailScreen(
                    prestamoId = key.prestamoId,
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
                    onCuotaClick = { cuotaId -> backStack.add(Screen.CobroPago(cuotaId)) }
                )
            }

            entry<Screen.CobrosList> {
                CobrosScreen(
                    onCobrarCuota = { cuotaId -> backStack.add(Screen.CobroPago(cuotaId)) },
                    onIrAPrestamos = {
                        if (backStack.lastOrNull() != Screen.PrestamoList) {
                            backStack.clear()
                            backStack.add(Screen.PrestamoList)
                        }
                    }
                )
            }

            entry<Screen.CobroPago> { key ->
                CobroPagoScreen(
                    cuotaId = key.cuotaId,
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
                    onPagoRealizado = { pagoId ->
                        if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
                        backStack.add(Screen.Recibo(pagoId))
                    }
                )
            }

            entry<Screen.Recibo> { key ->
                ReciboScreen(
                    pagoId = key.pagoId,
                    onCerrar = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) }
                )
            }
        }
    )
}