package edu.ucne.credifast.presentation.cliente.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.usecase.ObserveClientesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteListViewModel @Inject constructor(
    private val observeClientesUseCase: ObserveClientesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClienteListUiState())
    val state: StateFlow<ClienteListUiState> = _state.asStateFlow()

    init {
        observarClientes()
    }

    fun onEvent(event: ClienteListUiEvent) {
        when (event) {
            is ClienteListUiEvent.FiltroChanged ->
                _state.update { it.copy(filtro = event.texto) }
        }
    }

    private fun observarClientes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeClientesUseCase().collect { lista ->
                _state.update { it.copy(clientes = lista, isLoading = false) }
            }
        }
    }
}