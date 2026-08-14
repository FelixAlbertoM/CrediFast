package edu.ucne.credifast.presentation.listanegra

import edu.ucne.credifast.domain.cliente.model.Cliente

data class ListaNegraUiState(
    val clientes: List<Cliente> = emptyList(),
    val filtro: String = "",
    val isLoading: Boolean = false
) {
    val clientesFiltrados: List<Cliente>
        get() = if (filtro.isBlank()) clientes
        else clientes.filter {
            it.nombre.contains(filtro, ignoreCase = true) ||
                    it.cedula.contains(filtro) ||
                    it.telefono.contains(filtro)
        }
}