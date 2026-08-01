package edu.ucne.credifast.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prestamos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["clienteId"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["clienteId"])]
)
data class PrestamoEntity(
    @PrimaryKey(autoGenerate = true)
    val prestamoId: Int = 0,
    val clienteId: Int,
    val capital: Double,
    val interesPorcentaje: Double,
    val cantidadCuotas: Int,
    val montoTotal: Double,
    val montoCuota: Double,
    val balancePendiente: Double,
    val estado: String = "ACTIVO",
    val fechaOtorgado: Long = System.currentTimeMillis()
)