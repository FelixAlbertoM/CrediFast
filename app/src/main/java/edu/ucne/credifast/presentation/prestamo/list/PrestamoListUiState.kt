package edu.ucne.credifast.presentation.prestamo.list

enum class FiltroPrestamo { ACTIVOS, SALDADOS, EN_MORA }

data class PrestamoListUiState(
    val prestamos: List<PrestamoListItem> = emptyList(),
    val filtro: String = "",
    val chipSeleccionado: FiltroPrestamo = FiltroPrestamo.ACTIVOS,
    val isLoading: Boolean = false
) {
    val prestamosFiltrados: List<PrestamoListItem>
        get() {
            val porChip = when (chipSeleccionado) {
                FiltroPrestamo.ACTIVOS -> prestamos.filter { it.estado == "ACTIVO" }
                FiltroPrestamo.SALDADOS -> prestamos.filter { it.estado == "SALDADO" }
                FiltroPrestamo.EN_MORA -> prestamos.filter { it.cuotasVencidas > 0 }
            }
            return if (filtro.isBlank()) porChip
            else porChip.filter { it.nombreCliente.contains(filtro, ignoreCase = true) }
        }

    val totalEnLaCalle: Double
        get() = prestamos.filter { it.estado == "ACTIVO" }.sumOf { it.balancePendiente }
}