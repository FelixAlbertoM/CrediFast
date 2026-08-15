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
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import edu.ucne.credifast.domain.prestamo.usecase.ObservePrestamosUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClienteListViewModel @Inject constructor(
    private val observeClientesUseCase: ObserveClientesUseCase,
    private val observePrestamosUseCase: ObservePrestamosUseCase,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClienteListUiState())
    val state: StateFlow<ClienteListUiState> = _state.asStateFlow()

    init {
        observar()
    }

    fun onEvent(event: ClienteListUiEvent) {
        when (event) {
            is ClienteListUiEvent.FiltroChanged ->
                _state.update { it.copy(filtro = event.texto) }
            is ClienteListUiEvent.ChipChanged ->
                _state.update { it.copy(chipSeleccionado = event.chip) }
        }
    }

    private fun observar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeClientesUseCase()
                .flatMapLatest { clientes ->
                    val activos = observePrestamosUseCase()
                    combine(
                        flow { emit(clientes) },
                        activos
                    ) { listaClientes, prestamos ->
                        val activosPorCliente = prestamos
                            .filter { it.estado == "ACTIVO" }
                            .associateBy { it.clienteId }

                        listaClientes.map { cliente ->
                            val prestamoActivo = activosPorCliente[cliente.clienteId]
                            val estado = when {
                                cliente.enListaNegra -> EstadoCliente.LISTA_NEGRA
                                prestamoActivo == null -> EstadoCliente.SIN_PRESTAMO
                                else -> EstadoCliente.AL_DIA
                            }
                            ClienteListItem(
                                cliente = cliente,
                                estado = estado,
                                tienePrestamoActivo = prestamoActivo != null
                            )
                        }
                    }
                }
                .collect { items ->
                    _state.update { it.copy(clientes = items, isLoading = false) }
                }
        }
    }
}