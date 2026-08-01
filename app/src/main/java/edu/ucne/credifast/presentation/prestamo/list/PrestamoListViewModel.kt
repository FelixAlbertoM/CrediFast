package edu.ucne.credifast.presentation.prestamo.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.usecase.ObserveClientesUseCase
import edu.ucne.credifast.domain.prestamo.usecase.ObservePrestamosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrestamoListViewModel @Inject constructor(
    private val observePrestamosUseCase: ObservePrestamosUseCase,
    private val observeClientesUseCase: ObserveClientesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PrestamoListUiState())
    val state: StateFlow<PrestamoListUiState> = _state.asStateFlow()

    init {
        observar()
    }

    fun onEvent(event: PrestamoListUiEvent) {
        when (event) {
            is PrestamoListUiEvent.FiltroChanged ->
                _state.update { it.copy(filtro = event.texto) }
        }
    }

    private fun observar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            combine(
                observePrestamosUseCase(),
                observeClientesUseCase()
            ) { prestamos, clientes ->
                val porId = clientes.associateBy { it.clienteId }
                prestamos.map { p ->
                    val c = porId[p.clienteId]
                    PrestamoUi(
                        prestamo = p,
                        nombreCliente = c?.nombre ?: "Cliente #${p.clienteId}",
                        cedulaCliente = c?.cedula ?: "",
                        telefonoCliente = c?.telefono ?: ""
                    )
                }
            }.collect { lista ->
                _state.update { it.copy(prestamos = lista, isLoading = false) }
            }
        }
    }
}