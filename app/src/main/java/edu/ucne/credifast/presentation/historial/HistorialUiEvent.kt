package edu.ucne.credifast.presentation.historial

sealed interface HistorialUiEvent {
    data class FiltroChanged(val texto: String) : HistorialUiEvent
    data class OrdenChanged(val orden: OrdenHistorial) : HistorialUiEvent
    data class FechaSeleccionada(val fecha: Long?) : HistorialUiEvent
}