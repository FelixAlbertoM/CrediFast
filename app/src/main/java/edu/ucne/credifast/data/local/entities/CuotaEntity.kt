package edu.ucne.credifast.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cuotas",
    foreignKeys = [
        ForeignKey(
            entity = PrestamoEntity::class,
            parentColumns = ["prestamoId"],
            childColumns = ["prestamoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["prestamoId"])]
)
data class CuotaEntity(
    @PrimaryKey(autoGenerate = true)
    val cuotaId: Int = 0,
    val prestamoId: Int,
    val numeroCuota: Int,
    val fechaVencimiento: Long,
    val monto: Double,
    val montoPagado: Double = 0.0,
    val estado: String = "PENDIENTE",
    val fechaPago: Long? = null
)