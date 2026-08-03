package edu.ucne.credifast.domain.cobro.usecase

import edu.ucne.credifast.domain.cobro.model.Pago
import edu.ucne.credifast.domain.cobro.repository.CobroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePagosDelDiaUseCase @Inject constructor(
    private val repository: CobroRepository
) {
    operator fun invoke(inicio: Long, fin: Long): Flow<List<Pago>> =
        repository.observePagosEntreFechas(inicio, fin)
}