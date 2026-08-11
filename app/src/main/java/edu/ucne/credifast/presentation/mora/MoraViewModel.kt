package edu.ucne.credifast.presentation.mora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.mora.usecase.GetClientesEnMoraUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoraViewModel @Inject constructor(
    private val getClientesEnMoraUseCase: GetClientesEnMoraUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MoraUiState())
    val state: StateFlow<MoraUiState> = _state.asStateFlow()

    init {
        observar()
    }

    private fun observar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getClientesEnMoraUseCase().collect { lista ->
                _state.update {
                    it.copy(
                        clientesEnMora = lista,
                        moraTotal = lista.sumOf { c -> c.moraAcumulada },
                        isLoading = false
                    )
                }
            }
        }
    }
}