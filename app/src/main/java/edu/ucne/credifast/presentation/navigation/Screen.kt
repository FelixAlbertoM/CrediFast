package edu.ucne.credifast.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Main : Screen()

    @Serializable
    data class ClienteEdit(val clienteId: Int = 0) : Screen()

    @Serializable
    data object PrestamoEdit : Screen()

    @Serializable
    data class PrestamoDetail(val prestamoId: Int) : Screen()

    @Serializable
    data class CobroPago(val cuotaId: Int) : Screen()

    @Serializable
    data class Recibo(val pagoId: Int) : Screen()
}