package edu.ucne.credifast.presentation.historial

import edu.ucne.credifast.domain.historial.model.PrestamoHistorial

enum class OrdenHistorial { RECIENTES, ANTIGUOS, POR_FECHA }

data class HistorialUiState(
    val prestamos: List<PrestamoHistorial> = emptyList(),
    val filtro: String = "",
    val orden: OrdenHistorial = OrdenHistorial.RECIENTES,
    val fechaSeleccionada: Long? = null,
    val isLoading: Boolean = false
) {
    val prestamosFiltrados: List<PrestamoHistorial>
        get() {
            val porTexto = if (filtro.isBlank()) prestamos
            else prestamos.filter {
                it.nombreCliente.contains(filtro, ignoreCase = true) ||
                        it.cedulaCliente.contains(filtro) ||
                        it.telefonoCliente.contains(filtro)
            }

            val porFecha = if (orden == OrdenHistorial.POR_FECHA && fechaSeleccionada != null) {
                val inicio = inicioDelDia(fechaSeleccionada)
                val fin = finDelDia(fechaSeleccionada)
                porTexto.filter { it.fechaOtorgado in inicio..fin }
            } else porTexto

            return when (orden) {
                OrdenHistorial.RECIENTES -> porFecha.sortedByDescending { it.fechaOtorgado }
                OrdenHistorial.ANTIGUOS -> porFecha.sortedBy { it.fechaOtorgado }
                OrdenHistorial.POR_FECHA -> porFecha.sortedByDescending { it.fechaOtorgado }
            }
        }

    private fun inicioDelDia(fecha: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = fecha
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun finDelDia(fecha: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = fecha
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 59)
        cal.set(java.util.Calendar.SECOND, 59)
        cal.set(java.util.Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}