package edu.ucne.credifast.data.cobro

import edu.ucne.credifast.data.local.dao.CuotaDao
import edu.ucne.credifast.data.local.dao.PagoDao
import edu.ucne.credifast.data.local.dao.PrestamoDao
import edu.ucne.credifast.data.mapper.toDomain
import edu.ucne.credifast.domain.cobro.model.Pago
import edu.ucne.credifast.domain.cobro.repository.CobroRepository
import edu.ucne.credifast.domain.cobro.usecase.CobroCalculator
import edu.ucne.credifast.domain.common.Resource
import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.data.local.entities.PagoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CobroRepositoryImpl @Inject constructor(
    private val cuotaDao: CuotaDao,
    private val prestamoDao: PrestamoDao,
    private val pagoDao: PagoDao
) : CobroRepository {

    override fun observeCuotasDelDia(inicioDia: Long, finDia: Long): Flow<List<Cuota>> =
        cuotaDao.observeEntreFechas(inicioDia, finDia).map { lista -> lista.map { it.toDomain() } }

    override fun observePagosEntreFechas(inicio: Long, fin: Long): Flow<List<Pago>> =
        pagoDao.observeEntreFechas(inicio, fin).map { lista -> lista.map { it.toDomain() } }

    override fun observePagosDePrestamo(prestamoId: Int): Flow<List<Pago>> =
        pagoDao.observeByPrestamo(prestamoId).map { lista -> lista.map { it.toDomain() } }

    override suspend fun realizarCobro(
        cuotaId: Int,
        montoCobrado: Double,
        tipo: String,
        nota: String?
    ): Resource<Pago> {
        if (montoCobrado <= 0) return Resource.Error("El monto debe ser mayor que cero")

        val cuota = cuotaDao.getById(cuotaId)
            ?: return Resource.Error("La cuota no existe")
        val prestamoEntity = prestamoDao.getById(cuota.prestamoId)
            ?: return Resource.Error("El préstamo no existe")
        val prestamo = prestamoEntity.toDomain()

        return try {
            val capital = CobroCalculator.capitalDe(montoCobrado, prestamo)
            val interes = CobroCalculator.interesDe(montoCobrado, prestamo)
            val nuevoBalance = CobroCalculator.nuevoBalance(prestamo.balancePendiente, montoCobrado)
            val saldado = nuevoBalance <= 0.0

            prestamoDao.upsert(
                prestamoEntity.copy(
                    balancePendiente = nuevoBalance,
                    estado = if (saldado) "SALDADO" else "ACTIVO"
                )
            )

            if (tipo == "SALDO_TOTAL" || saldado) {
                val pendientes = cuotaDao.getByPrestamo(cuota.prestamoId)
                    .filter { it.estado != "PAGADA" }
                pendientes.forEach { c ->
                    cuotaDao.upsert(
                        c.copy(
                            estado = "PAGADA",
                            montoPagado = c.monto,
                            fechaPago = System.currentTimeMillis()
                        )
                    )
                }
            } else {
                cuotaDao.upsert(
                    cuota.copy(
                        estado = "PAGADA",
                        montoPagado = cuota.monto,
                        fechaPago = System.currentTimeMillis()
                    )
                )
            }

            val pagoEntity = PagoEntity(
                prestamoId = cuota.prestamoId,
                numeroCuota = cuota.numeroCuota,
                montoCobrado = montoCobrado,
                capital = capital,
                interes = interes,
                mora = 0.0,
                balanceRestante = nuevoBalance,
                nota = nota,
                tipo = tipo
            )
            val nuevoId = pagoDao.upsert(pagoEntity).toInt()

            Resource.Success(pagoEntity.copy(pagoId = nuevoId).toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "No se pudo realizar el cobro")
        }
    }
}