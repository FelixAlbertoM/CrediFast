package edu.ucne.credifast.domain.prestamo.usecase

import edu.ucne.credifast.domain.common.Resource
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import javax.inject.Inject

class OtorgarPrestamoUseCase @Inject constructor(
    private val repository: PrestamoRepository
) {
    suspend operator fun invoke(
        clienteId: Int,
        capital: Double,
        interesPorcentaje: Double,
        cantidadCuotas: Int
    ): Resource<Unit> = repository.otorgarPrestamo(
        clienteId = clienteId,
        capital = capital,
        interesPorcentaje = interesPorcentaje,
        cantidadCuotas = cantidadCuotas
    )
}