package edu.ucne.credifast.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.credifast.data.local.dao.ClienteDao
import edu.ucne.credifast.data.local.entities.ClienteEntity
import edu.ucne.credifast.data.local.dao.UsuarioDao
import edu.ucne.credifast.data.local.entities.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        ClienteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CrediFastDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
}