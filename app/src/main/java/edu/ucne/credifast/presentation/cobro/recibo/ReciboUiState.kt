package edu.ucne.credifast.presentation.cobro.recibo

import edu.ucne.credifast.domain.cobro.usecase.ReciboData

data class ReciboUiState(
    val recibo: ReciboData? = null,
    val isLoading: Boolean = true
)