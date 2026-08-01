package edu.ucne.credifast.domain.prestamo.model

data class Prestamo(
    val prestamoId: Int = 0,
    val clienteId: Int,
    val capital: Double,
    val interesPorcentaje: Double,
    val cantidadCuotas: Int,
    val montoTotal: Double,
    val montoCuota: Double,
    val balancePendiente: Double,
    val estado: String = "ACTIVO",
    val fechaOtorgado: Long = System.currentTimeMillis()
)