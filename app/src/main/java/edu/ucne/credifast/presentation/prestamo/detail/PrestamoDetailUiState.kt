package edu.ucne.credifast.presentation.prestamo.detail

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.model.Prestamo

data class PrestamoDetailUiState(
    val prestamo: Prestamo? = null,
    val cliente: Cliente? = null,
    val cuotas: List<Cuota> = emptyList(),
    val isLoading: Boolean = false
) {
    val moraTotal: Double
        get() = cuotas.sumOf { it.mora() }
}