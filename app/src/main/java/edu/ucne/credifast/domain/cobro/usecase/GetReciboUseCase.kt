package edu.ucne.credifast.domain.cobro.usecase

import edu.ucne.credifast.data.local.dao.CuotaDao
import edu.ucne.credifast.data.local.dao.PagoDao
import edu.ucne.credifast.data.mapper.toDomain
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import javax.inject.Inject

data class ReciboData(
    val nombreEmpresa: String,
    val fechaPago: Long,
    val nombreCliente: String,
    val cedulaCliente: String,
    val numeroCuota: Int,
    val totalCuotas: Int,
    val montoCobrado: Double,
    val capital: Double,
    val interes: Double,
    val balanceRestante: Double,
    val proximoVencimiento: Long?
)

class GetReciboUseCase @Inject constructor(
    private val pagoDao: PagoDao,
    private val cuotaDao: CuotaDao,
    private val prestamoRepository: PrestamoRepository,
    private val clienteRepository: ClienteRepository
) {
    suspend operator fun invoke(pagoId: Int): ReciboData? {
        val pago = pagoDao.getById(pagoId)?.toDomain() ?: return null
        val prestamo = prestamoRepository.getPrestamo(pago.prestamoId) ?: return null
        val cliente = clienteRepository.getCliente(prestamo.clienteId)

        val proximaCuota = cuotaDao.getByPrestamo(pago.prestamoId)
            .map { it.toDomain() }
            .filter { !it.estaPagada }
            .minByOrNull { it.fechaVencimiento }

        return ReciboData(
            nombreEmpresa = "FR_CrediFast",
            fechaPago = pago.fechaPago,
            nombreCliente = cliente?.nombre ?: "Cliente",
            cedulaCliente = cliente?.cedula ?: "",
            numeroCuota = pago.numeroCuota,
            totalCuotas = prestamo.cantidadCuotas,
            montoCobrado = pago.montoCobrado,
            capital = pago.capital,
            interes = pago.interes,
            balanceRestante = pago.balanceRestante,
            proximoVencimiento = proximaCuota?.fechaVencimiento
        )
    }
}