package edu.ucne.credifast.presentation.mora

import edu.ucne.credifast.domain.mora.model.ClienteEnMora

data class MoraUiState(
    val clientesEnMora: List<ClienteEnMora> = emptyList(),
    val moraTotal: Double = 0.0,
    val isLoading: Boolean = false
)