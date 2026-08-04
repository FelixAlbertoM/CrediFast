package edu.ucne.credifast.domain.cobro.usecase

import edu.ucne.credifast.domain.prestamo.model.Prestamo

object CobroCalculator {

    fun capitalDe(montoCobrado: Double, prestamo: Prestamo): Double {
        if (prestamo.montoTotal <= 0) return 0.0
        val fraccion = prestamo.capital / prestamo.montoTotal
        return montoCobrado * fraccion
    }

    fun interesDe(montoCobrado: Double, prestamo: Prestamo): Double {
        if (prestamo.montoTotal <= 0) return 0.0
        val interesTotal = prestamo.montoTotal - prestamo.capital
        val fraccion = interesTotal / prestamo.montoTotal
        return montoCobrado * fraccion
    }

    fun nuevoBalance(balanceActual: Double, montoCobrado: Double): Double =
        (balanceActual - montoCobrado).coerceAtLeast(0.0)

    fun quedaSaldado(balanceActual: Double, montoCobrado: Double): Boolean =
        nuevoBalance(balanceActual, montoCobrado) <= 0.0
}