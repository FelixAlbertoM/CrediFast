package edu.ucne.credifast.presentation.cliente.edit

sealed interface ClienteEditUiEvent {
    data class NombreChanged(val v: String) : ClienteEditUiEvent
    data class CedulaChanged(val v: String) : ClienteEditUiEvent
    data class TelefonoChanged(val v: String) : ClienteEditUiEvent
    data class DireccionChanged(val v: String) : ClienteEditUiEvent
    data object Guardar : ClienteEditUiEvent
    data object Eliminar : ClienteEditUiEvent
    data object MensajeErrorMostrado : ClienteEditUiEvent
    data object NavegacionRealizada : ClienteEditUiEvent
}