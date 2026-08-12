package edu.ucne.credifast.domain.mora.usecase

import edu.ucne.credifast.domain.cliente.usecase.ObserveClientesUseCase
import edu.ucne.credifast.domain.mora.model.ClienteEnMora
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetClientesEnMoraUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val observeClientesUseCase: ObserveClientesUseCase
) {
    operator fun invoke(): Flow<List<ClienteEnMora>> =
        prestamoRepository.observePrestamosPorEstado("ACTIVO")
            .flatMapLatest { prestamosActivos ->
                if (prestamosActivos.isEmpty()) {
                    flow { emit(emptyList<ClienteEnMora>()) }
                } else {
                    val flujosCuotas = prestamosActivos.map { prestamo ->
                        prestamoRepository.observeCuotas(prestamo.prestamoId)
                    }
                    combine(
                        combine(flujosCuotas) { it.toList() },
                        observeClientesUseCase()
                    ) { listasCuotas, clientes ->
                        val clientesPorId = clientes.associateBy { it.clienteId }
                        val resultado = mutableListOf<ClienteEnMora>()

                        prestamosActivos.forEachIndexed { index, prestamo ->
                            val cuotas = listasCuotas[index]
                            val vencidas = cuotas.filter { it.diasAtraso() > 0 }
                            if (vencidas.isNotEmpty()) {
                                val cliente = clientesPorId[prestamo.clienteId]
                                resultado.add(
                                    ClienteEnMora(
                                        clienteId = prestamo.clienteId,
                                        prestamoId = prestamo.prestamoId,
                                        nombreCliente = cliente?.nombre ?: "Cliente #${prestamo.clienteId}",
                                        cedulaCliente = cliente?.cedula ?: "",
                                        cuotasVencidas = vencidas.size,
                                        diasMaximoAtraso = vencidas.maxOf { it.diasAtraso() },
                                        moraAcumulada = vencidas.sumOf { it.mora() }
                                    )
                                )
                            }
                        }
                        resultado.sortedByDescending { it.moraAcumulada }
                    }
                }
            }
}