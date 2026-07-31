package edu.ucne.credifast.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "clientes",
    indices = [
        Index(value = ["cedula"], unique = true),
        Index(value = ["telefono"], unique = true)
    ]
)
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val clienteId: Int = 0,
    val nombre: String,
    val cedula: String,
    val telefono: String,
    val direccion: String,
    val enListaNegra: Boolean = false,
    val razonListaNegra: String? = null,
    val fechaListaNegra: Long? = null,
    val fechaRegistro: Long = System.currentTimeMillis()
)