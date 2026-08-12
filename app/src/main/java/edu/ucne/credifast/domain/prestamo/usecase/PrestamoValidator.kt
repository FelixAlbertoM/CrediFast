package edu.ucne.credifast.domain.prestamo.usecase

import edu.ucne.credifast.domain.cliente.model.Cliente

object PrestamoValidator {

    fun validarCliente(cliente: Cliente?): String? =
        if (cliente == null) "Debes seleccionar un cliente" else null

    fun validarCapital(capital: String): String? {
        val n = capital.toDoubleOrNull()
        return when {
            capital.isBlank() -> "El monto es obligatorio"
            n == null -> "Monto inválido"
            n <= 0 -> "El monto debe ser mayor que 0"
            else -> null
        }
    }

    fun validarInteres(interes: String): String? {
        val n = interes.toDoubleOrNull()
        return when {
            interes.isBlank() -> "El interés es obligatorio"
            n == null -> "Interés inválido"
            n <= 0 -> "El interés debe ser mayor que 0"
            else -> null
        }
    }

    fun validarCuotas(cuotas: String): String? {
        val n = cuotas.toIntOrNull()
        return when {
            cuotas.isBlank() -> "Las cuotas son obligatorias"
            n == null -> "Cantidad inválida"
            n <= 0 -> "Debe haber al menos 1 cuota"
            else -> null
        }
    }
}