package edu.ucne.credifast.presentation.cobro.list

sealed interface CobrosUiEvent {
    data class DiaSeleccionado(val fecha: Long) : CobrosUiEvent
}