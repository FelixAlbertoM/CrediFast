package edu.ucne.credifast.domain.prestamo.usecase

import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.model.Prestamo
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object PrestamoCalculator {

    private val UNA_SEMANA = TimeUnit.DAYS.toMillis(7)
    fun calcularMontoTotal(capital: Double, interesPorcentaje: Double): Double =
        capital + (capital * interesPorcentaje / 100.0)

    fun calcularMontoCuota(montoTotal: Double, cantidadCuotas: Int): Double =
        if (cantidadCuotas <= 0) 0.0
        else (montoTotal / cantidadCuotas).roundToInt().toDouble()

    fun construirPrestamo(
        clienteId: Int,
        capital: Double,
        interesPorcentaje: Double,
        cantidadCuotas: Int,
        fechaOtorgado: Long = System.currentTimeMillis()
    ): Prestamo {
        val total = calcularMontoTotal(capital, interesPorcentaje)
        val cuota = calcularMontoCuota(total, cantidadCuotas)
        return Prestamo(
            clienteId = clienteId,
            capital = capital,
            interesPorcentaje = interesPorcentaje,
            cantidadCuotas = cantidadCuotas,
            montoTotal = total,
            montoCuota = cuota,
            balancePendiente = total,
            estado = "ACTIVO",
            fechaOtorgado = fechaOtorgado
        )
    }

    fun generarCuotas(
        prestamoId: Int,
        montoTotal: Double,
        montoCuota: Double,
        cantidadCuotas: Int,
        fechaOtorgado: Long
    ): List<Cuota> {
        val cuotas = mutableListOf<Cuota>()
        var acumulado = 0.0

        for (numero in 1..cantidadCuotas) {
            val esUltima = numero == cantidadCuotas
            val monto = if (esUltima) montoTotal - acumulado else montoCuota
            acumulado += monto

            val fechaVencimiento = fechaOtorgado + (UNA_SEMANA * numero)

            cuotas.add(
                Cuota(
                    prestamoId = prestamoId,
                    numeroCuota = numero,
                    fechaVencimiento = fechaVencimiento,
                    monto = monto
                )
            )
        }
        return cuotas
    }
}