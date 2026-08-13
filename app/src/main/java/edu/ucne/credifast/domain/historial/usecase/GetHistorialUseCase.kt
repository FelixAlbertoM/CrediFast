package edu.ucne.credifast.domain.historial.usecase

import edu.ucne.credifast.domain.cliente.usecase.ObserveClientesUseCase
import edu.ucne.credifast.domain.historial.model.PrestamoHistorial
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetHistorialUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val observeClientesUseCase: ObserveClientesUseCase
) {
    operator fun invoke(): Flow<List<PrestamoHistorial>> =
        combine(
            prestamoRepository.observePrestamosPorEstado("SALDADO"),
            observeClientesUseCase()
        ) { prestamos, clientes ->
            val clientesPorId = clientes.associateBy { it.clienteId }
            prestamos
                .sortedByDescending { it.fechaOtorgado }
                .map { p ->
                    val cliente = clientesPorId[p.clienteId]
                    PrestamoHistorial(
                        prestamoId = p.prestamoId,
                        nombreCliente = cliente?.nombre ?: "Cliente #${p.clienteId}",
                        cedulaCliente = cliente?.cedula ?: "",
                        telefonoCliente = cliente?.telefono ?: "",
                        capital = p.capital,
                        interesPorcentaje = p.interesPorcentaje,
                        montoTotal = p.montoTotal,
                        cantidadCuotas = p.cantidadCuotas,
                        fechaOtorgado = p.fechaOtorgado
                    )
                }
        }
}