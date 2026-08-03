package edu.ucne.credifast.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.credifast.data.local.entities.CuotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CuotaDao {

    @Upsert
    suspend fun upsert(cuota: CuotaEntity)

    @Upsert
    suspend fun upsertAll(cuotas: List<CuotaEntity>)

    @Query("SELECT * FROM cuotas WHERE prestamoId = :prestamoId ORDER BY numeroCuota ASC")
    fun observeByPrestamo(prestamoId: Int): Flow<List<CuotaEntity>>

    @Query("SELECT * FROM cuotas WHERE prestamoId = :prestamoId ORDER BY numeroCuota ASC")
    suspend fun getByPrestamo(prestamoId: Int): List<CuotaEntity>

    @Query("SELECT * FROM cuotas WHERE cuotaId = :id LIMIT 1")
    suspend fun getById(id: Int): CuotaEntity?
}