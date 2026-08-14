package edu.ucne.credifast.domain.historial.model

data class PrestamoHistorial(
    val prestamoId: Int,
    val nombreCliente: String,
    val cedulaCliente: String,
    val telefonoCliente: String,
    val capital: Double,
    val interesPorcentaje: Double,
    val montoTotal: Double,
    val cantidadCuotas: Int,
    val fechaOtorgado: Long
)