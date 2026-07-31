package edu.ucne.credifast.presentation.cliente.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteEditScreen(
    clienteId: Int,
    onBack: () -> Unit,
    viewModel: ClienteEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(clienteId) { viewModel.cargarCliente(clienteId) }

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            viewModel.onEvent(ClienteEditUiEvent.NavegacionRealizada)
            onBack()
        }
    }
    LaunchedEffect(state.mensajeError) {
        state.mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(ClienteEditUiEvent.MensajeErrorMostrado)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.esEdicion) "Editar cliente" else "Nuevo cliente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onEvent(ClienteEditUiEvent.NombreChanged(it)) },
                label = { Text("Nombre completo *") },
                isError = state.errorNombre != null,
                supportingText = { state.errorNombre?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.cedula,
                onValueChange = { viewModel.onEvent(ClienteEditUiEvent.CedulaChanged(it)) },
                label = { Text("Cédula * (11 dígitos)") },
                isError = state.errorCedula != null,
                supportingText = { state.errorCedula?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.telefono,
                onValueChange = { viewModel.onEvent(ClienteEditUiEvent.TelefonoChanged(it)) },
                label = { Text("Teléfono * (10 dígitos)") },
                isError = state.errorTelefono != null,
                supportingText = { state.errorTelefono?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.direccion,
                onValueChange = { viewModel.onEvent(ClienteEditUiEvent.DireccionChanged(it)) },
                label = { Text("Dirección *") },
                isError = state.errorDireccion != null,
                supportingText = { state.errorDireccion?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.onEvent(ClienteEditUiEvent.Guardar) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if (state.esEdicion) "Guardar cambios" else "Guardar cliente")
            }

            if (state.esEdicion) {
                OutlinedButton(
                    onClick = { viewModel.onEvent(ClienteEditUiEvent.Eliminar) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Text("  Eliminar cliente")
                }
            }
        }
    }
}