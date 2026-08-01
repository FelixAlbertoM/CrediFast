package edu.ucne.credifast.presentation.prestamo.list

sealed interface PrestamoListUiEvent {
    data class FiltroChanged(val texto: String) : PrestamoListUiEvent
}