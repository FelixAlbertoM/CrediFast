package edu.ucne.credifast.domain.cliente.usecase

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import edu.ucne.credifast.domain.common.Resource
import javax.inject.Inject

class EliminarClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(cliente: Cliente): Resource<Unit> =
        repository.eliminarCliente(cliente)
}