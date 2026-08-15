package edu.ucne.credifast.presentation.prestamo.list

data class PrestamoListItem(
    val prestamoId: Int,
    val nombreCliente: String,
    val estado: String,
    val balancePendiente: Double,
    val numeroCuotaActual: Int,
    val totalCuotas: Int,
    val fechaProximoVencimiento: Long?,
    val cuotasVencidas: Int,
    val moraAcumulada: Double
)