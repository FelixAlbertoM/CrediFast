package edu.ucne.credifast.domain.cobro.model

enum class TipoPago { CUOTA_COMPLETA, SALDO_TOTAL, ABONO_LIBRE }

data class Pago(
    val pagoId: Int = 0,
    val prestamoId: Int,
    val numeroCuota: Int,
    val montoCobrado: Double,
    val capital: Double,
    val interes: Double,
    val mora: Double = 0.0,
    val balanceRestante: Double,
    val nota: String? = null,
    val tipo: String = "CUOTA_COMPLETA",
    val fechaPago: Long = System.currentTimeMillis()
)