package edu.ucne.credifast.presentation.cliente.list

sealed interface ClienteListUiEvent {
    data class FiltroChanged(val texto: String) : ClienteListUiEvent
    data class ChipChanged(val chip: FiltroCliente) : ClienteListUiEvent
}