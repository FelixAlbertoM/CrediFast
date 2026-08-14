package edu.ucne.credifast.presentation.listanegra

import edu.ucne.credifast.domain.cliente.model.Cliente

sealed interface ListaNegraUiEvent {
    data class FiltroChanged(val texto: String) : ListaNegraUiEvent
    data class QuitarDeListaNegra(val cliente: Cliente) : ListaNegraUiEvent
}