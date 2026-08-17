package edu.ucne.credifast.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import edu.ucne.credifast.presentation.auth.LoginScreen
import edu.ucne.credifast.presentation.cliente.edit.ClienteEditScreen
import edu.ucne.credifast.presentation.cobro.pago.CobroPagoScreen
import edu.ucne.credifast.presentation.cobro.recibo.ReciboScreen
import edu.ucne.credifast.presentation.historial.HistorialScreen
import edu.ucne.credifast.presentation.main.MainScreen
import edu.ucne.credifast.presentation.mora.MoraScreen
import edu.ucne.credifast.presentation.prestamo.detail.PrestamoDetailScreen
import edu.ucne.credifast.presentation.prestamo.edit.PrestamoEditScreen
import edu.ucne.credifast.presentation.listanegra.ListaNegraScreen

@Composable
fun CrediFastNavHost(isLoggedIn: Boolean) {
    val backStack = rememberNavBackStack(
        if (isLoggedIn) Screen.Main else Screen.Login
    )

    LaunchedEffect(isLoggedIn) {
        val root = if (isLoggedIn) Screen.Main else Screen.Login
        if (backStack.isEmpty() || backStack[0] != root) {
            backStack.clear()
            backStack.add(root)
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
        entryProvider = entryProvider {

            entry<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(Screen.Main)
                    }
                )
            }

            entry<Screen.Main> {
                MainScreen(
                    onAgregarCliente = { backStack.add(Screen.ClienteEdit(0)) },
                    onClienteClick = { id -> backStack.add(Screen.ClienteEdit(id)) },
                    onOtorgarPrestamo = { backStack.add(Screen.PrestamoEdit) },
                    onPrestamoClick = { id -> backStack.add(Screen.PrestamoDetail(id)) },
                    onCobrarCuota = { cuotaId -> backStack.add(Screen.CobroPago(cuotaId)) },
                    onIrAMora = { backStack.add(Screen.Mora) },
                    onIrAHistorial = { backStack.add(Screen.Historial) },
                    onIrAListaNegra = { backStack.add(Screen.ListaNegra) },
                    onCerrarSesion = {
                        backStack.clear()
                        backStack.add(Screen.Login)
                    }
                )
            }

            entry<Screen.ClienteEdit> { key ->
                ClienteEditScreen(
                    clienteId = key.clienteId,
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) }
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

            entry<Screen.Mora> {
                MoraScreen(
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
                    onIrACobrar = { prestamoId -> backStack.add(Screen.PrestamoDetail(prestamoId)) },
                    onVerDetalle = { prestamoId -> backStack.add(Screen.PrestamoDetail(prestamoId)) }
                )
            }

            entry<Screen.Historial> {
                HistorialScreen(
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
                    onPrestamoClick = { prestamoId -> backStack.add(Screen.PrestamoDetail(prestamoId)) }
                )
            }

            entry<Screen.ListaNegra> {
                ListaNegraScreen(
                    onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) }
                )
            }
        }
    )
}