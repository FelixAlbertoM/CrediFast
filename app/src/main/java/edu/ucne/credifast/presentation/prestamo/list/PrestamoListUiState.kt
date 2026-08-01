package edu.ucne.credifast.presentation.prestamo.list

import edu.ucne.credifast.domain.prestamo.model.Prestamo

data class PrestamoUi(
    val prestamo: Prestamo,
    val nombreCliente: String,
    val cedulaCliente: String,
    val telefonoCliente: String
)

data class PrestamoListUiState(
    val prestamos: List<PrestamoUi> = emptyList(),
    val filtro: String = "",
    val isLoading: Boolean = false
) {
    val prestamosFiltrados: List<PrestamoUi>
        get() = if (filtro.isBlank()) prestamos
        else prestamos.filter {
            it.nombreCliente.contains(filtro, ignoreCase = true) ||
                    it.cedulaCliente.contains(filtro) ||
                    it.telefonoCliente.contains(filtro)
        }
}