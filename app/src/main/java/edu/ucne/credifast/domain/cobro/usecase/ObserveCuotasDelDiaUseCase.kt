package edu.ucne.credifast.domain.cobro.usecase

import edu.ucne.credifast.domain.cobro.repository.CobroRepository
import edu.ucne.credifast.domain.prestamo.model.Cuota
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCuotasDelDiaUseCase @Inject constructor(
    private val repository: CobroRepository
) {
    operator fun invoke(inicioDia: Long, finDia: Long): Flow<List<Cuota>> =
        repository.observeCuotasDelDia(inicioDia, finDia)
}