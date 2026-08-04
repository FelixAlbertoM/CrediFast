package edu.ucne.credifast.domain.cobro.usecase

import edu.ucne.credifast.domain.cobro.model.Pago
import edu.ucne.credifast.domain.cobro.repository.CobroRepository
import edu.ucne.credifast.domain.common.Resource
import javax.inject.Inject

class RealizarCobroUseCase @Inject constructor(
    private val repository: CobroRepository
) {
    suspend operator fun invoke(
        cuotaId: Int,
        montoCobrado: Double,
        tipo: String,
        nota: String?
    ): Resource<Pago> = repository.realizarCobro(cuotaId, montoCobrado, tipo, nota)
}