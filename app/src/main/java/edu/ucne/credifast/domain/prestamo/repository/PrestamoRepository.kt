package edu.ucne.credifast.domain.prestamo.repository

import edu.ucne.credifast.domain.common.Resource
import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.model.Prestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun observePrestamos(): Flow<List<Prestamo>>
    fun observePrestamosPorEstado(estado: String): Flow<List<Prestamo>>
    suspend fun getPrestamo(id: Int): Prestamo?
    fun observeCuotas(prestamoId: Int): Flow<List<Cuota>>
    fun observePrestamosDeCliente(clienteId: Int): Flow<List<Prestamo>>

    suspend fun otorgarPrestamo(
        clienteId: Int,
        capital: Double,
        interesPorcentaje: Double,
        cantidadCuotas: Int
    ): Resource<Unit>

    suspend fun clienteTienePrestamoActivo(clienteId: Int): Boolean
}