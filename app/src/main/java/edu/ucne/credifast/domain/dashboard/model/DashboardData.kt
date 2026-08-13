package edu.ucne.credifast.domain.dashboard.model

data class RecaudacionDia(
    val etiqueta: String,
    val monto: Double,
    val esHoy: Boolean
)

data class DashboardData(
    val pendienteEnCalle: Double = 0.0,
    val desembolsado: Double = 0.0,
    val recaudado7Dias: Double = 0.0,
    val prestamosActivos: Int = 0,
    val clientesEnMora: Int = 0,
    val recaudacionSemana: List<RecaudacionDia> = emptyList()
)