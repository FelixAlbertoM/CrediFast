package edu.ucne.credifast.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.credifast.data.local.dao.UsuarioDao
import edu.ucne.credifast.data.local.entities.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CrediFastDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}