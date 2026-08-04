package edu.ucne.credifast.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pagos",
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
data class PagoEntity(
    @PrimaryKey(autoGenerate = true)
    val pagoId: Int = 0,
    val prestamoId: Int,
    val numeroCuota: Int,
    val montoCobrado: Double,
    val capital: Double,
    val interes: Double,
    val mora: Double = 0.0,
    val balanceRestante: Double,
    val nota: String? = null,
    val tipo: String = "CUOTA_COMPLETA",
    val fechaPago: Long = System.currentTimeMillis()
)