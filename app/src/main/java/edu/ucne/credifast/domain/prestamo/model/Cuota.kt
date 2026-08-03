package edu.ucne.credifast.domain.prestamo.model
import java.util.concurrent.TimeUnit
enum class EstadoCuota { PAGADA, PENDIENTE, RETRASADA }

data class Cuota(
    val cuotaId: Int = 0,
    val prestamoId: Int,
    val numeroCuota: Int,
    val fechaVencimiento: Long,
    val monto: Double,
    val montoPagado: Double = 0.0,
    val estado: String = "PENDIENTE",
    val fechaPago: Long? = null
) {
    companion object {
        const val MORA_POR_DIA = 100.0
    }

    val estaPagada: Boolean get() = estado == "PAGADA"

    fun diasAtraso(ahora: Long = System.currentTimeMillis()): Int {
        if (estaPagada || ahora <= fechaVencimiento) return 0
        val diff = ahora - fechaVencimiento
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    fun mora(ahora: Long = System.currentTimeMillis()): Double =
        diasAtraso(ahora) * MORA_POR_DIA

    fun estadoActual(ahora: Long = System.currentTimeMillis()): EstadoCuota = when {
        estaPagada -> EstadoCuota.PAGADA
        diasAtraso(ahora) > 0 -> EstadoCuota.RETRASADA
        else -> EstadoCuota.PENDIENTE
    }
}