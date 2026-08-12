package edu.ucne.credifast.presentation.prestamo.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.common.Resource
import edu.ucne.credifast.domain.prestamo.usecase.GetClientesElegiblesUseCase
import edu.ucne.credifast.domain.prestamo.usecase.OtorgarPrestamoUseCase
import edu.ucne.credifast.domain.prestamo.usecase.PrestamoValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrestamoEditViewModel @Inject constructor(
    private val getClientesElegiblesUseCase: GetClientesElegiblesUseCase,
    private val otorgarPrestamoUseCase: OtorgarPrestamoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PrestamoEditUiState())
    val state: StateFlow<PrestamoEditUiState> = _state.asStateFlow()

    init {
        cargarClientesElegibles()
    }

    fun reiniciar() {
        _state.update {
            PrestamoEditUiState(clientesElegibles = it.clientesElegibles)
        }
    }

    fun onEvent(event: PrestamoEditUiEvent) {
        when (event) {
            is PrestamoEditUiEvent.FiltroClienteChanged ->
                _state.update { it.copy(filtroCliente = event.v) }

            is PrestamoEditUiEvent.ClienteSeleccionado ->
                _state.update { it.copy(clienteSeleccionado = event.cliente) }

            is PrestamoEditUiEvent.CapitalChanged ->
                _state.update { it.copy(capital = event.v.filter { c -> c.isDigit() }) }

            is PrestamoEditUiEvent.InteresChanged ->
                _state.update { it.copy(interes = event.v.filter { c -> c.isDigit() || c == '.' }) }

            is PrestamoEditUiEvent.CuotasChanged ->
                _state.update { it.copy(cuotas = event.v.filter { c -> c.isDigit() }) }

            PrestamoEditUiEvent.Otorgar -> otorgar()
            PrestamoEditUiEvent.MensajeMostrado -> _state.update { it.copy(errorMensaje = null) }
            PrestamoEditUiEvent.NavegacionRealizada -> _state.update { it.copy(guardadoExitoso = false) }
            PrestamoEditUiEvent.DeseleccionarCliente ->
                _state.update { it.copy(clienteSeleccionado = null) }
        }
    }

    private fun cargarClientesElegibles() {
        viewModelScope.launch {
            getClientesElegiblesUseCase().collect { lista ->
                _state.update { it.copy(clientesElegibles = lista) }
            }
        }
    }

    private fun otorgar() {
        val s = _state.value

        val eCliente = PrestamoValidator.validarCliente(s.clienteSeleccionado)
        val eCapital = PrestamoValidator.validarCapital(s.capital)
        val eInteres = PrestamoValidator.validarInteres(s.interes)
        val eCuotas = PrestamoValidator.validarCuotas(s.cuotas)

        if (eCliente != null || eCapital != null || eInteres != null || eCuotas != null) {
            _state.update {
                it.copy(
                    errorCliente = eCliente,
                    errorCapital = eCapital,
                    errorInteres = eInteres,
                    errorCuotas = eCuotas
                )
            }
            return
        }

        val cliente = s.clienteSeleccionado ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = otorgarPrestamoUseCase(
                clienteId = cliente.clienteId,
                capital = s.capital.toDoubleOrNull() ?: 0.0,
                interesPorcentaje = s.interes.toDoubleOrNull() ?: 0.0,
                cantidadCuotas = s.cuotas.toIntOrNull() ?: 0
            )
            when (result) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
                is Resource.Error -> _state.update { it.copy(isLoading = false, errorMensaje = result.message) }
                Resource.Loading -> {}
            }
        }
    }
}