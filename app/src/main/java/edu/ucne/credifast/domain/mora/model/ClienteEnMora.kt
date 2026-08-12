package edu.ucne.credifast.domain.mora.model

data class ClienteEnMora(
    val clienteId: Int,
    val prestamoId: Int,
    val nombreCliente: String,
    val cedulaCliente: String,
    val cuotasVencidas: Int,
    val diasMaximoAtraso: Int,
    val moraAcumulada: Double
)