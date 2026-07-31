package edu.ucne.credifast.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val uid: String,
    val nombre: String,
    val correo: String,
    val fotoUrl: String? = null,
    val fechaAcceso: Long = System.currentTimeMillis()
)