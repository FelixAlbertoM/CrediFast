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
                    onClienteClick = { id -> backStack.add(Screen.ClienteEdit(id)) }
                )
            }

            entry<Screen.ClienteEdit> { key ->
                ClienteEditScreen(
                    clienteId = key.clienteId,
                    onBack = {
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    }
                )
            }
        }
    )
}