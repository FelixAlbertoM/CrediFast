package edu.ucne.credifast.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.credifast.data.local.entities.PrestamoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrestamoDao {

    @Upsert
    suspend fun upsert(prestamo: PrestamoEntity): Long

    @Delete
    suspend fun delete(prestamo: PrestamoEntity)

    @Query("SELECT * FROM prestamos ORDER BY fechaOtorgado DESC")
    fun observeAll(): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamos WHERE estado = :estado ORDER BY fechaOtorgado DESC")
    fun observeByEstado(estado: String): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamos WHERE prestamoId = :id LIMIT 1")
    suspend fun getById(id: Int): PrestamoEntity?

    @Query("SELECT * FROM prestamos WHERE clienteId = :clienteId AND estado = 'ACTIVO' LIMIT 1")
    suspend fun getPrestamoActivoDeCliente(clienteId: Int): PrestamoEntity?
}