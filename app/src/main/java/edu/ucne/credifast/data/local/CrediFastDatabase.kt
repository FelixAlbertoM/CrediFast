package edu.ucne.credifast.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.credifast.data.local.dao.ClienteDao
import edu.ucne.credifast.data.local.dao.CuotaDao
import edu.ucne.credifast.data.local.dao.PagoDao
import edu.ucne.credifast.data.local.dao.PrestamoDao
import edu.ucne.credifast.data.local.dao.UsuarioDao
import edu.ucne.credifast.data.local.entities.ClienteEntity
import edu.ucne.credifast.data.local.entities.CuotaEntity
import edu.ucne.credifast.data.local.entities.PagoEntity
import edu.ucne.credifast.data.local.entities.PrestamoEntity
import edu.ucne.credifast.data.local.entities.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        ClienteEntity::class,
        PrestamoEntity::class,
        CuotaEntity::class,
        PagoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class CrediFastDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun prestamoDao(): PrestamoDao
    abstract fun cuotaDao(): CuotaDao
    abstract fun pagoDao(): PagoDao
}