package edu.ucne.credifast.presentation.cliente.list
enum class FiltroCliente { TODOS, CON_PRESTAMO, LISTA_NEGRA }

data class ClienteListUiState(
    val clientes: List<ClienteListItem> = emptyList(),
    val filtro: String = "",
    val chipSeleccionado: FiltroCliente = FiltroCliente.TODOS,
    val isLoading: Boolean = false
) {
    val clientesFiltrados: List<ClienteListItem>
        get() {
            val porChip = when (chipSeleccionado) {
                FiltroCliente.TODOS -> clientes
                FiltroCliente.CON_PRESTAMO -> clientes.filter { it.tienePrestamoActivo }
                FiltroCliente.LISTA_NEGRA -> clientes.filter { it.estado == EstadoCliente.LISTA_NEGRA }
            }
            return if (filtro.isBlank()) porChip
            else porChip.filter {
                it.cliente.nombre.contains(filtro, ignoreCase = true) ||
                        it.cliente.cedula.contains(filtro) ||
                        it.cliente.telefono.contains(filtro)
            }
        }

    val totalClientes: Int get() = clientes.size
}