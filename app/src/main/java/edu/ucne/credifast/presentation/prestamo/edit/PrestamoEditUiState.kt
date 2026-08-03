package edu.ucne.credifast.presentation.prestamo.edit

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.prestamo.usecase.PrestamoCalculator

data class PrestamoEditUiState(
    val clientesElegibles: List<Cliente> = emptyList(),
    val filtroCliente: String = "",
    val clienteSeleccionado: Cliente? = null,
    val capital: String = "",
    val interes: String = "",
    val cuotas: String = "",
    val errorMensaje: String? = null,
    val guardadoExitoso: Boolean = false,
    val isLoading: Boolean = false
) {
    val clientesFiltrados: List<Cliente>
        get() = if (filtroCliente.isBlank()) clientesElegibles
        else clientesElegibles.filter {
            it.nombre.contains(filtroCliente, ignoreCase = true) ||
                    it.cedula.contains(filtroCliente) ||
                    it.telefono.contains(filtroCliente)
        }

    private val capitalNum: Double get() = capital.toDoubleOrNull() ?: 0.0
    private val interesNum: Double get() = interes.toDoubleOrNull() ?: 0.0
    private val cuotasNum: Int get() = cuotas.toIntOrNull() ?: 0

    val montoTotal: Double
        get() = PrestamoCalculator.calcularMontoTotal(capitalNum, interesNum)

    val montoCuota: Double
        get() = PrestamoCalculator.calcularMontoCuota(montoTotal, cuotasNum)

    val puedeOtorgar: Boolean
        get() = clienteSeleccionado != null &&
                capitalNum > 0 && interesNum >= 0 && cuotasNum > 0 && !isLoading
}