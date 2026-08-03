package edu.ucne.credifast.data.prestamo

import edu.ucne.credifast.data.local.dao.ClienteDao
import edu.ucne.credifast.data.local.dao.CuotaDao
import edu.ucne.credifast.data.local.dao.PrestamoDao
import edu.ucne.credifast.data.mapper.toDomain
import edu.ucne.credifast.data.mapper.toEntity
import edu.ucne.credifast.domain.common.Resource
import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.model.Prestamo
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import edu.ucne.credifast.domain.prestamo.usecase.PrestamoCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrestamoRepositoryImpl @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val cuotaDao: CuotaDao,
    private val clienteDao: ClienteDao
) : PrestamoRepository {

    override fun observePrestamos(): Flow<List<Prestamo>> =
        prestamoDao.observeAll().map { lista -> lista.map { it.toDomain() } }

    override fun observePrestamosPorEstado(estado: String): Flow<List<Prestamo>> =
        prestamoDao.observeByEstado(estado).map { lista -> lista.map { it.toDomain() } }

    override suspend fun getPrestamo(id: Int): Prestamo? =
        prestamoDao.getById(id)?.toDomain()

    override fun observeCuotas(prestamoId: Int): Flow<List<Cuota>> =
        cuotaDao.observeByPrestamo(prestamoId).map { lista -> lista.map { it.toDomain() } }

    override suspend fun clienteTienePrestamoActivo(clienteId: Int): Boolean =
        prestamoDao.getPrestamoActivoDeCliente(clienteId) != null

    override suspend fun otorgarPrestamo(
        clienteId: Int,
        capital: Double,
        interesPorcentaje: Double,
        cantidadCuotas: Int
    ): Resource<Unit> {

        if (capital <= 0) return Resource.Error("El monto debe ser mayor que cero")
        if (interesPorcentaje < 0) return Resource.Error("El interés no puede ser negativo")
        if (cantidadCuotas <= 0) return Resource.Error("Debe indicar la cantidad de cuotas")

        val cliente = clienteDao.getById(clienteId)
            ?: return Resource.Error("El cliente no existe")
        if (cliente.enListaNegra) {
            return Resource.Error("El cliente está en lista negra y no puede recibir préstamos")
        }
        if (clienteTienePrestamoActivo(clienteId)) {
            return Resource.Error("El cliente ya tiene un préstamo activo")
        }

        return try {
            val prestamo = PrestamoCalculator.construirPrestamo(
                clienteId = clienteId,
                capital = capital,
                interesPorcentaje = interesPorcentaje,
                cantidadCuotas = cantidadCuotas
            )
            val nuevoId = prestamoDao.upsert(prestamo.toEntity()).toInt()

            val cuotas = PrestamoCalculator.generarCuotas(
                prestamoId = nuevoId,
                montoTotal = prestamo.montoTotal,
                montoCuota = prestamo.montoCuota,
                cantidadCuotas = cantidadCuotas,
                fechaOtorgado = prestamo.fechaOtorgado
            )
            cuotaDao.upsertAll(cuotas.map { it.toEntity() })

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "No se pudo otorgar el préstamo")
        }
    }
}