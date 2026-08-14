package edu.ucne.credifast.presentation.listanegra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.usecase.ObserveListaNegraUseCase
import edu.ucne.credifast.domain.cliente.usecase.ToggleListaNegraUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaNegraViewModel @Inject constructor(
    private val observeListaNegraUseCase: ObserveListaNegraUseCase,
    private val toggleListaNegraUseCase: ToggleListaNegraUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListaNegraUiState())
    val state: StateFlow<ListaNegraUiState> = _state.asStateFlow()

    init {
        observar()
    }

    fun onEvent(event: ListaNegraUiEvent) {
        when (event) {
            is ListaNegraUiEvent.FiltroChanged ->
                _state.update { it.copy(filtro = event.texto) }

            is ListaNegraUiEvent.QuitarDeListaNegra -> {
                viewModelScope.launch {
                    toggleListaNegraUseCase(
                        cliente = event.cliente,
                        enListaNegra = false,
                        razon = null
                    )
                }
            }
        }
    }

    private fun observar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeListaNegraUseCase().collect { lista ->
                _state.update { it.copy(clientes = lista, isLoading = false) }
            }
        }
    }
}