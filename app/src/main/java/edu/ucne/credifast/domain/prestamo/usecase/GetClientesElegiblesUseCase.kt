package edu.ucne.credifast.domain.prestamo.usecase

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetClientesElegiblesUseCase @Inject constructor(
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository
) {
    operator fun invoke(): Flow<List<Cliente>> =
        combine(
            clienteRepository.observeClientes(),
            prestamoRepository.observePrestamosPorEstado("ACTIVO")
        ) { clientes, prestamosActivos ->
            val idsConPrestamoActivo = prestamosActivos.map { it.clienteId }.toSet()
            clientes.filter { cliente ->
                !cliente.enListaNegra && cliente.clienteId !in idsConPrestamoActivo
            }
        }
}