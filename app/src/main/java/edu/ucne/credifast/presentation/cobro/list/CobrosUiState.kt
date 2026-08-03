package edu.ucne.credifast.presentation.cobro.list

import edu.ucne.credifast.domain.prestamo.model.Cuota

data class CobroItem(
    val cuota: Cuota,
    val nombreCliente: String,
    val cedulaCliente: String,
    val totalCuota: Int,
    val numeroCuota: Int,
    val totalCuotas: Int
)

data class CobrosUiState(
    val diaSeleccionado: Long = System.currentTimeMillis(),
    val cobros: List<CobroItem> = emptyList(),
    val totalPorCobrar: Double = 0.0,
    val totalCobrado: Double = 0.0,
    val isLoading: Boolean = false
)