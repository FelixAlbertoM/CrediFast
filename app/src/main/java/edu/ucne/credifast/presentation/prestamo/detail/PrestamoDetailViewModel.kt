package edu.ucne.credifast.presentation.prestamo.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.usecase.GetClienteUseCase
import edu.ucne.credifast.domain.prestamo.usecase.GetPrestamoUseCase
import edu.ucne.credifast.domain.prestamo.usecase.ObserveCuotasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrestamoDetailViewModel @Inject constructor(
    private val getPrestamoUseCase: GetPrestamoUseCase,
    private val getClienteUseCase: GetClienteUseCase,
    private val observeCuotasUseCase: ObserveCuotasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PrestamoDetailUiState())
    val state: StateFlow<PrestamoDetailUiState> = _state.asStateFlow()

    fun cargar(prestamoId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val prestamo = getPrestamoUseCase(prestamoId)
            val cliente = prestamo?.let { getClienteUseCase(it.clienteId) }
            _state.update { it.copy(prestamo = prestamo, cliente = cliente, isLoading = false) }
        }
        viewModelScope.launch {
            observeCuotasUseCase(prestamoId).collect { lista ->
                _state.update { it.copy(cuotas = lista) }
            }
        }
    }
}