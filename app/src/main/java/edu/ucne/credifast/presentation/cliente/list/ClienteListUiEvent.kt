package edu.ucne.credifast.presentation.cliente.list

sealed interface ClienteListUiEvent {
    data class FiltroChanged(val texto: String) : ClienteListUiEvent
}