package edu.ucne.credifast.presentation.cobro.recibo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cobro.usecase.GetReciboUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReciboViewModel @Inject constructor(
    private val getReciboUseCase: GetReciboUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReciboUiState())
    val state: StateFlow<ReciboUiState> = _state.asStateFlow()

    fun cargar(pagoId: Int) {
        viewModelScope.launch {
            val recibo = getReciboUseCase(pagoId)
            _state.update { it.copy(recibo = recibo, isLoading = false) }
        }
    }
}