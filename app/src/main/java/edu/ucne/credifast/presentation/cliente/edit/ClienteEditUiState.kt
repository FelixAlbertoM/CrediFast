package edu.ucne.credifast.presentation.cliente.edit


data class ClienteEditUiState(
    val clienteId: Int = 0,
    val nombre: String = "",
    val cedula: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val enListaNegra: Boolean = false,
    val razonListaNegra: String? = null,
    val fechaListaNegra: Long? = null,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val errorNombre: String? = null,
    val errorCedula: String? = null,
    val errorTelefono: String? = null,
    val errorDireccion: String? = null,
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val mensajeError: String? = null,
    val mostrarDialogoEliminar: Boolean = false,
    val mostrarDialogoListaNegra: Boolean = false,
    val noSePuedeEliminar: Boolean = false,
) {
    val esEdicion: Boolean get() = clienteId != 0
}