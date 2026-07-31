package edu.ucne.credifast.domain.cliente.model

data class Cliente(
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