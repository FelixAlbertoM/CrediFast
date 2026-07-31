package edu.ucne.credifast.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.credifast.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Upsert
    suspend fun upsert(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE uid = :uid LIMIT 1")
    suspend fun getByUid(uid: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios ORDER BY fechaAcceso DESC")
    fun observeAll(): Flow<List<UsuarioEntity>>
}