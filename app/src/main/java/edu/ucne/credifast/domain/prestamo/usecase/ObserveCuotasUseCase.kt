package edu.ucne.credifast.domain.prestamo.usecase

import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCuotasUseCase @Inject constructor(
    private val repository: PrestamoRepository
) {
    operator fun invoke(prestamoId: Int): Flow<List<Cuota>> =
        repository.observeCuotas(prestamoId)
}