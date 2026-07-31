package edu.ucne.credifast.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.credifast.data.local.entities.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Upsert
    suspend fun upsert(cliente: ClienteEntity)

    @Delete
    suspend fun delete(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun observeAll(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE clienteId = :id LIMIT 1")
    suspend fun getById(id: Int): ClienteEntity?

    @Query("SELECT * FROM clientes WHERE cedula = :cedula LIMIT 1")
    suspend fun getByCedula(cedula: String): ClienteEntity?

    @Query("SELECT * FROM clientes WHERE telefono = :telefono LIMIT 1")
    suspend fun getByTelefono(telefono: String): ClienteEntity?
}