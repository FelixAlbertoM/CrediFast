package edu.ucne.credifast.domain.auth.model

data class UsuarioAuth(
    val uid: String,
    val nombre: String,
    val correo: String,
    val fotoUrl: String?
)