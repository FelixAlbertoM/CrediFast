package edu.ucne.credifast.presentation.cliente.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.usecase.ClienteValidator
import edu.ucne.credifast.domain.cliente.usecase.EliminarClienteUseCase
import edu.ucne.credifast.domain.cliente.usecase.GetClienteUseCase
import edu.ucne.credifast.domain.cliente.usecase.GuardarClienteUseCase
import edu.ucne.credifast.domain.common.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteEditViewModel @Inject constructor(
    private val getClienteUseCase: GetClienteUseCase,
    private val guardarClienteUseCase: GuardarClienteUseCase,
    private val eliminarClienteUseCase: EliminarClienteUseCase,
    private val toggleListaNegraUseCase: edu.ucne.credifast.domain.cliente.usecase.ToggleListaNegraUseCase,
    private val clienteTienePrestamosUseCase: edu.ucne.credifast.domain.cliente.usecase.ClienteTienePrestamosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClienteEditUiState())
    val state: StateFlow<ClienteEditUiState> = _state.asStateFlow()

    fun cargarCliente(clienteId: Int) {
        if (clienteId == 0) {
            _state.value = ClienteEditUiState()
            return
        }
        viewModelScope.launch {
            getClienteUseCase(clienteId)?.let { c ->
                _state.update {
                    it.copy(
                        clienteId = c.clienteId,
                        nombre = c.nombre,
                        cedula = c.cedula,
                        telefono = c.telefono,
                        direccion = c.direccion,
                        enListaNegra = c.enListaNegra,
                        razonListaNegra = c.razonListaNegra,
                        fechaListaNegra = c.fechaListaNegra,
                        fechaRegistro = c.fechaRegistro
                    )
                }
            }
        }
    }

    fun onEvent(event: ClienteEditUiEvent) {
        when (event) {
            is ClienteEditUiEvent.NombreChanged -> _state.update {
                it.copy(nombre = event.v, errorNombre = ClienteValidator.validarNombre(event.v))
            }
            is ClienteEditUiEvent.CedulaChanged -> {
                val limpio = event.v.filter { c -> c.isDigit() }.take(11)
                _state.update {
                    it.copy(cedula = limpio, errorCedula = ClienteValidator.validarCedula(limpio))
                }
            }
            is ClienteEditUiEvent.RazonListaNegraChanged ->
                _state.update { it.copy(razonListaNegra = event.v) }

            ClienteEditUiEvent.NavegacionRealizada ->
                _state.update { it.copy(guardadoExitoso = false) }

            is ClienteEditUiEvent.TelefonoChanged -> {
                val limpio = event.v.filter { c -> c.isDigit() }.take(10)
                _state.update {
                    it.copy(telefono = limpio, errorTelefono = ClienteValidator.validarTelefono(limpio))
                }
            }
            is ClienteEditUiEvent.DireccionChanged -> _state.update {
                it.copy(direccion = event.v, errorDireccion = ClienteValidator.validarDireccion(event.v))
            }
            ClienteEditUiEvent.Guardar -> guardar()
            ClienteEditUiEvent.SolicitarEliminar -> solicitarEliminar()
            ClienteEditUiEvent.ConfirmarEliminar -> eliminar()
            ClienteEditUiEvent.CancelarEliminar ->
                _state.update { it.copy(mostrarDialogoEliminar = false) }
            ClienteEditUiEvent.SolicitarToggleListaNegra ->
                _state.update { it.copy(mostrarDialogoListaNegra = true) }
            ClienteEditUiEvent.ConfirmarToggleListaNegra -> {
                _state.update { it.copy(mostrarDialogoListaNegra = false) }
                toggleListaNegra()
            }
            ClienteEditUiEvent.CancelarToggleListaNegra ->
                _state.update { it.copy(mostrarDialogoListaNegra = false) }
            ClienteEditUiEvent.MensajeNoEliminableMostrado ->
                _state.update { it.copy(noSePuedeEliminar = false) }
            ClienteEditUiEvent.MensajeErrorMostrado -> _state.update { it.copy(mensajeError = null) }
            ClienteEditUiEvent.Eliminar -> solicitarEliminar()
            ClienteEditUiEvent.ToggleListaNegra -> toggleListaNegra()
        }
    }

    private fun guardar() {
        val s = _state.value
        val eNombre = ClienteValidator.validarNombre(s.nombre)
        val eCedula = ClienteValidator.validarCedula(s.cedula)
        val eTelefono = ClienteValidator.validarTelefono(s.telefono)
        val eDireccion = ClienteValidator.validarDireccion(s.direccion)

        if (eNombre != null || eCedula != null || eTelefono != null || eDireccion != null) {
            _state.update {
                it.copy(
                    errorNombre = eNombre,
                    errorCedula = eCedula,
                    errorTelefono = eTelefono,
                    errorDireccion = eDireccion
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val cliente = Cliente(
                clienteId = s.clienteId,
                nombre = s.nombre.trim(),
                cedula = s.cedula,
                telefono = s.telefono,
                direccion = s.direccion.trim(),
                enListaNegra = s.enListaNegra,
                razonListaNegra = s.razonListaNegra,
                fechaListaNegra = s.fechaListaNegra,
                fechaRegistro = s.fechaRegistro
            )
            when (val r = guardarClienteUseCase(cliente)) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
                is Resource.Error -> _state.update { it.copy(isLoading = false, mensajeError = r.message) }
                Resource.Loading -> {}
            }
        }
    }


    private fun solicitarEliminar() {
        val id = _state.value.clienteId
        if (id == 0) return
        viewModelScope.launch {
            if (clienteTienePrestamosUseCase(id)) {
                _state.update { it.copy(noSePuedeEliminar = true) }
            } else {
                _state.update { it.copy(mostrarDialogoEliminar = true) }
            }
        }
    }

    private fun eliminar() {
        val s = _state.value
        viewModelScope.launch {
            val cliente = Cliente(
                clienteId = s.clienteId,
                nombre = s.nombre,
                cedula = s.cedula,
                telefono = s.telefono,
                direccion = s.direccion,
                enListaNegra = s.enListaNegra,
                razonListaNegra = s.razonListaNegra,
                fechaListaNegra = s.fechaListaNegra,
                fechaRegistro = s.fechaRegistro
            )
            eliminarClienteUseCase(cliente)
            _state.update { it.copy(mostrarDialogoEliminar = false, guardadoExitoso = true) }
        }
    }
    
    private fun toggleListaNegra() {
        val s = _state.value
        if (s.clienteId == 0) return
        viewModelScope.launch {
            val cliente = Cliente(
                clienteId = s.clienteId,
                nombre = s.nombre,
                cedula = s.cedula,
                telefono = s.telefono,
                direccion = s.direccion,
                enListaNegra = s.enListaNegra,
                razonListaNegra = s.razonListaNegra,
                fechaListaNegra = s.fechaListaNegra,
                fechaRegistro = s.fechaRegistro
            )
            val nuevoEstado = !s.enListaNegra
            toggleListaNegraUseCase(
                cliente = cliente,
                enListaNegra = nuevoEstado,
                razon = if (nuevoEstado) s.razonListaNegra else null
            )
            _state.update {
                it.copy(
                    enListaNegra = nuevoEstado,
                    razonListaNegra = if (nuevoEstado) it.razonListaNegra else null
                )
            }
        }
    }
}