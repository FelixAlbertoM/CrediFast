package edu.ucne.credifast.domain.cobro.repository

import edu.ucne.credifast.domain.cobro.model.Pago
import edu.ucne.credifast.domain.common.Resource
import edu.ucne.credifast.domain.prestamo.model.Cuota
import kotlinx.coroutines.flow.Flow

interface CobroRepository {

    fun observeCuotasDelDia(inicioDia: Long, finDia: Long): Flow<List<Cuota>>

    fun observePagosEntreFechas(inicio: Long, fin: Long): Flow<List<Pago>>

    fun observePagosDePrestamo(prestamoId: Int): Flow<List<Pago>>

    suspend fun realizarCobro(
        cuotaId: Int,
        montoCobrado: Double,
        tipo: String,
        nota: String?
    ): Resource<Pago>
}