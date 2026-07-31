package edu.ucne.credifast.domain.cliente.usecase

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import edu.ucne.credifast.domain.common.Resource
import javax.inject.Inject

class ToggleListaNegraUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(
        cliente: Cliente,
        enListaNegra: Boolean,
        razon: String?
    ): Resource<Unit> {
        val actualizado = cliente.copy(
            enListaNegra = enListaNegra,
            razonListaNegra = if (enListaNegra) razon else null,
            fechaListaNegra = if (enListaNegra) System.currentTimeMillis() else null
        )
        return repository.guardarCliente(actualizado)
    }
}