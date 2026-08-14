package edu.ucne.credifast.presentation.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.historial.usecase.GetHistorialUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val getHistorialUseCase: GetHistorialUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistorialUiState())
    val state: StateFlow<HistorialUiState> = _state.asStateFlow()

    init {
        observar()
    }

    fun onEvent(event: HistorialUiEvent) {
        when (event) {
            is HistorialUiEvent.FiltroChanged ->
                _state.update { it.copy(filtro = event.texto) }

            is HistorialUiEvent.OrdenChanged ->
                _state.update {
                    it.copy(
                        orden = event.orden,
                        fechaSeleccionada = if (event.orden != OrdenHistorial.POR_FECHA) null
                        else it.fechaSeleccionada
                    )
                }

            is HistorialUiEvent.FechaSeleccionada ->
                _state.update { it.copy(fechaSeleccionada = event.fecha) }
        }
    }

    private fun observar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getHistorialUseCase().collect { lista ->
                _state.update { it.copy(prestamos = lista, isLoading = false) }
            }
        }
    }
}