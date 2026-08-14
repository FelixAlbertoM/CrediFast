package edu.ucne.credifast.domain.cliente.usecase

import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ClienteTienePrestamosUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(clienteId: Int): Boolean =
        prestamoRepository.observePrestamosDeCliente(clienteId).first().isNotEmpty()
}