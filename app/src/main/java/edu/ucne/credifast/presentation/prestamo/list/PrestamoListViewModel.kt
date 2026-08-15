package edu.ucne.credifast.presentation.prestamo.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.usecase.ObserveClientesUseCase
import edu.ucne.credifast.domain.prestamo.usecase.ObservePrestamosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PrestamoListViewModel @Inject constructor(
    private val observePrestamosUseCase: ObservePrestamosUseCase,
    private val observeClientesUseCase: ObserveClientesUseCase,
    private val prestamoRepository: PrestamoRepository
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
            is PrestamoListUiEvent.ChipChanged ->
                _state.update { it.copy(chipSeleccionado = event.chip) }
        }
    }

    private fun observar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observePrestamosUseCase()
                .flatMapLatest { prestamos ->
                    if (prestamos.isEmpty()) {
                        flow { emit(emptyList<PrestamoListItem>()) }
                    } else {
                        val flujosCuotas = prestamos.map { p ->
                            prestamoRepository.observeCuotas(p.prestamoId)
                        }
                        combine(
                            combine(flujosCuotas) { it.toList() },
                            observeClientesUseCase()
                        ) { listasCuotas, clientes ->
                            val clientesPorId = clientes.associateBy { it.clienteId }
                            prestamos.mapIndexed { index, prestamo ->
                                val cuotas = listasCuotas[index]
                                val vencidas = cuotas.filter { it.diasAtraso() > 0 }
                                val proxima = cuotas
                                    .filter { !it.estaPagada }
                                    .minByOrNull { it.fechaVencimiento }
                                val pagadas = cuotas.count { it.estaPagada }
                                val cliente = clientesPorId[prestamo.clienteId]
                                PrestamoListItem(
                                    prestamoId = prestamo.prestamoId,
                                    nombreCliente = cliente?.nombre ?: "Cliente #${prestamo.clienteId}",
                                    estado = prestamo.estado,
                                    balancePendiente = prestamo.balancePendiente,
                                    numeroCuotaActual = (pagadas + 1).coerceAtMost(prestamo.cantidadCuotas),
                                    totalCuotas = prestamo.cantidadCuotas,
                                    fechaProximoVencimiento = proxima?.fechaVencimiento,
                                    cuotasVencidas = vencidas.size,
                                    moraAcumulada = vencidas.sumOf { it.mora() }
                                )
                            }
                        }
                    }
                }
                .collect { items ->
                    _state.update { it.copy(prestamos = items, isLoading = false) }
                }
        }
    }
}
