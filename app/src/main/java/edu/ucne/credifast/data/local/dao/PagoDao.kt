package edu.ucne.credifast.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.credifast.data.local.entities.PagoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoDao {

    @Upsert
    suspend fun upsert(pago: PagoEntity): Long

    @Query("SELECT * FROM pagos ORDER BY fechaPago DESC")
    fun observeAll(): Flow<List<PagoEntity>>

    @Query("SELECT * FROM pagos WHERE prestamoId = :prestamoId ORDER BY fechaPago DESC")
    fun observeByPrestamo(prestamoId: Int): Flow<List<PagoEntity>>

    @Query("SELECT * FROM pagos WHERE pagoId = :id LIMIT 1")
    suspend fun getById(id: Int): PagoEntity?

    @Query("SELECT * FROM pagos WHERE fechaPago BETWEEN :inicio AND :fin ORDER BY fechaPago DESC")
    fun observeEntreFechas(inicio: Long, fin: Long): Flow<List<PagoEntity>>
}