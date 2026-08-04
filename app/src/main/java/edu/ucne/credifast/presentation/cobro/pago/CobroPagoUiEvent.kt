package edu.ucne.credifast.presentation.cobro.pago

sealed interface CobroPagoUiEvent {
    data class MontoChanged(val v: String) : CobroPagoUiEvent
    data class NotaChanged(val v: String) : CobroPagoUiEvent
    data object PagarCuotaCompleta : CobroPagoUiEvent
    data object SaldarPrestamo : CobroPagoUiEvent
    data object PagarMontoLibre : CobroPagoUiEvent
    data object MensajeMostrado : CobroPagoUiEvent
}