package edu.ucne.credifast.domain.prestamo.usecase

import edu.ucne.credifast.domain.prestamo.model.Prestamo
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePrestamosUseCase @Inject constructor(
    private val repository: PrestamoRepository
) {
    operator fun invoke(): Flow<List<Prestamo>> = repository.observePrestamos()
}