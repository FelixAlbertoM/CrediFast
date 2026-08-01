package edu.ucne.credifast.presentation.prestamo.edit

import edu.ucne.credifast.domain.cliente.model.Cliente

sealed interface PrestamoEditUiEvent {
    data class FiltroClienteChanged(val v: String) : PrestamoEditUiEvent
    data class ClienteSeleccionado(val cliente: Cliente) : PrestamoEditUiEvent
    data class CapitalChanged(val v: String) : PrestamoEditUiEvent
    data class InteresChanged(val v: String) : PrestamoEditUiEvent
    data class CuotasChanged(val v: String) : PrestamoEditUiEvent
    data object Otorgar : PrestamoEditUiEvent
    data object MensajeMostrado : PrestamoEditUiEvent
    data object NavegacionRealizada : PrestamoEditUiEvent
    data object DeseleccionarCliente : PrestamoEditUiEvent
}