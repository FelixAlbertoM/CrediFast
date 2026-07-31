package edu.ucne.credifast.domain.cliente.usecase

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveClientesUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    operator fun invoke(): Flow<List<Cliente>> = repository.observeClientes()
}