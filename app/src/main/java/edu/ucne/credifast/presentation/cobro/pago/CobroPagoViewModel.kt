package edu.ucne.credifast.presentation.cobro.pago

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.usecase.GetClienteUseCase
import edu.ucne.credifast.domain.cobro.usecase.GetCuotaConDetalleUseCase
import edu.ucne.credifast.domain.cobro.usecase.RealizarCobroUseCase
import edu.ucne.credifast.domain.common.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CobroPagoViewModel @Inject constructor(
    private val getCuotaConDetalleUseCase: GetCuotaConDetalleUseCase,
    private val getClienteUseCase: GetClienteUseCase,
    private val realizarCobroUseCase: RealizarCobroUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CobroPagoUiState())
    val state: StateFlow<CobroPagoUiState> = _state.asStateFlow()

    fun cargar(cuotaId: Int) {
        viewModelScope.launch {
            val detalle = getCuotaConDetalleUseCase(cuotaId) ?: return@launch
            val cliente = getClienteUseCase(detalle.prestamo.clienteId)
            _state.update {
                CobroPagoUiState(
                    cuotaId = detalle.cuota.cuotaId,
                    nombreCliente = cliente?.nombre ?: "Cliente",
                    numeroCuota = detalle.cuota.numeroCuota,
                    totalCuotas = detalle.prestamo.cantidadCuotas,
                    montoCuota = detalle.cuota.monto,
                    balancePendiente = detalle.prestamo.balancePendiente,
                    montoIngresado = detalle.cuota.monto.toInt().toString()
                )
            }
        }
    }

    fun onEvent(event: CobroPagoUiEvent) {
        when (event) {
            is CobroPagoUiEvent.MontoChanged ->
                _state.update { it.copy(montoIngresado = event.v.filter { c -> c.isDigit() }) }

            is CobroPagoUiEvent.NotaChanged ->
                _state.update { it.copy(nota = event.v) }

            CobroPagoUiEvent.PagarCuotaCompleta -> {
                val s = _state.value
                cobrar(s.montoCuota, "CUOTA_COMPLETA")
            }

            CobroPagoUiEvent.SaldarPrestamo -> {
                val s = _state.value
                cobrar(s.balancePendiente, "SALDO_TOTAL")
            }

            CobroPagoUiEvent.PagarMontoLibre -> {
                val s = _state.value
                val monto = s.montoIngresado.toDoubleOrNull() ?: 0.0
                cobrar(monto, "ABONO_LIBRE")
            }

            CobroPagoUiEvent.MensajeMostrado ->
                _state.update { it.copy(errorMensaje = null) }
        }
    }

    private fun cobrar(monto: Double, tipo: String) {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val nota = s.nota.ifBlank { null }
            when (val r = realizarCobroUseCase(s.cuotaId, monto, tipo, nota)) {
                is Resource.Success -> _state.update {
                    it.copy(isLoading = false, pagoRealizadoId = r.data.pagoId)
                }
                is Resource.Error -> _state.update {
                    it.copy(isLoading = false, errorMensaje = r.message)
                }
                Resource.Loading -> {}
            }
        }
    }
}