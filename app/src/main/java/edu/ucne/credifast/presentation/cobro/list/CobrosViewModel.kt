package edu.ucne.credifast.presentation.cobro.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.usecase.ObserveClientesUseCase
import edu.ucne.credifast.domain.cobro.usecase.ObserveCuotasDelDiaUseCase
import edu.ucne.credifast.domain.cobro.usecase.ObservePagosDelDiaUseCase
import edu.ucne.credifast.domain.cobro.usecase.RangoDia
import edu.ucne.credifast.domain.prestamo.usecase.ObservePrestamosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CobrosViewModel @Inject constructor(
    private val observeCuotasDelDiaUseCase: ObserveCuotasDelDiaUseCase,
    private val observePagosDelDiaUseCase: ObservePagosDelDiaUseCase,
    private val observePrestamosUseCase: ObservePrestamosUseCase,
    private val observeClientesUseCase: ObserveClientesUseCase
) : ViewModel() {

    private val _diaSeleccionado = MutableStateFlow(System.currentTimeMillis())
    private val _state = MutableStateFlow(CobrosUiState())
    val state: StateFlow<CobrosUiState> = _state.asStateFlow()

    init {
        observar()
    }

    fun onEvent(event: CobrosUiEvent) {
        when (event) {
            is CobrosUiEvent.DiaSeleccionado -> {
                _diaSeleccionado.value = event.fecha
                _state.update { it.copy(diaSeleccionado = event.fecha) }
            }
        }
    }

    private fun observar() {
        viewModelScope.launch {
            _diaSeleccionado.flatMapLatest { dia ->
                val inicio = RangoDia.inicioDe(dia)
                val fin = RangoDia.finDe(dia)
                combine(
                    observeCuotasDelDiaUseCase(inicio, fin),
                    observePagosDelDiaUseCase(inicio, fin),
                    observePrestamosUseCase(),
                    observeClientesUseCase()
                ) { cuotas, pagos, prestamos, clientes ->
                    val prestamosPorId = prestamos.associateBy { it.prestamoId }
                    val clientesPorId = clientes.associateBy { it.clienteId }

                    val items = cuotas.mapNotNull { cuota ->
                        val prestamo = prestamosPorId[cuota.prestamoId] ?: return@mapNotNull null
                        if (prestamo.estado != "ACTIVO") return@mapNotNull null
                        val cliente = clientesPorId[prestamo.clienteId]
                        CobroItem(
                            cuota = cuota,
                            nombreCliente = cliente?.nombre ?: "Cliente #${prestamo.clienteId}",
                            cedulaCliente = cliente?.cedula ?: "",
                            totalCuota = cuota.monto.toInt(),
                            numeroCuota = cuota.numeroCuota,
                            totalCuotas = prestamo.cantidadCuotas
                        )
                    }

                    val porCobrar = items.filter { !it.cuota.estaPagada }.sumOf { it.cuota.monto }
                    val cobrado = pagos.sumOf { it.montoCobrado }

                    CobrosUiState(
                        diaSeleccionado = dia,
                        cobros = items,
                        totalPorCobrar = porCobrar,
                        totalCobrado = cobrado,
                        isLoading = false
                    )
                }
            }.collect { nuevo ->
                _state.value = nuevo
            }
        }
    }
}