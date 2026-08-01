package edu.ucne.credifast.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object ClienteList : Screen()

    @Serializable
    data class ClienteEdit(val clienteId: Int = 0) : Screen()
    @Serializable
    data object PrestamoList : Screen()

    @Serializable
    data object PrestamoEdit : Screen()

    @Serializable
    data class PrestamoDetail(val prestamoId: Int) : Screen()
}