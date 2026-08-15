package edu.ucne.credifast.presentation.cliente.list

import edu.ucne.credifast.domain.cliente.model.Cliente

enum class EstadoCliente { AL_DIA, EN_MORA, SIN_PRESTAMO, LISTA_NEGRA }

data class ClienteListItem(
    val cliente: Cliente,
    val estado: EstadoCliente,
    val tienePrestamoActivo: Boolean
)