package edu.ucne.credifast.domain.prestamo.usecase

import edu.ucne.credifast.domain.prestamo.model.Prestamo
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import javax.inject.Inject

class GetPrestamoUseCase @Inject constructor(
    private val repository: PrestamoRepository
) {
    suspend operator fun invoke(id: Int): Prestamo? = repository.getPrestamo(id)
}