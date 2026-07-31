package edu.ucne.credifast.domain.cliente.usecase

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import javax.inject.Inject

class GetClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(id: Int): Cliente? = repository.getCliente(id)
}