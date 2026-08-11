package edu.ucne.credifast.presentation.prestamo.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.credifast.domain.cliente.model.Cliente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoEditScreen(
    onBack: () -> Unit,
    viewModel: PrestamoEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.reiniciar()
    }

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            viewModel.onEvent(PrestamoEditUiEvent.NavegacionRealizada)
            onBack()
        }
    }
    LaunchedEffect(state.errorMensaje) {
        state.errorMensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(PrestamoEditUiEvent.MensajeMostrado)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo préstamo") },
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
            Text("Cliente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            if (state.clienteSeleccionado == null) {
                OutlinedTextField(
                    value = state.filtroCliente,
                    onValueChange = { viewModel.onEvent(PrestamoEditUiEvent.FiltroClienteChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar cliente…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
                Card {
                    if (state.clientesFiltrados.isEmpty()) {
                        Text(
                            "No hay clientes disponibles. Solo aparecen los que no están en lista negra ni tienen un préstamo activo.",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                            items(state.clientesFiltrados, key = { it.clienteId }) { cliente ->
                                ClienteSelectItem(cliente) {
                                    viewModel.onEvent(PrestamoEditUiEvent.ClienteSeleccionado(cliente))
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            } else {
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(state.clienteSeleccionado!!.nombre, fontWeight = FontWeight.SemiBold)
                            Text(
                                state.clienteSeleccionado!!.cedula,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "Cambiar",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                viewModel.onEvent(PrestamoEditUiEvent.DeseleccionarCliente)
                            }
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.capital,
                    onValueChange = { viewModel.onEvent(PrestamoEditUiEvent.CapitalChanged(it)) },
                    label = { Text("Monto (RD$)") },
                    isError = state.errorCapital != null,
                    supportingText = { state.errorCapital?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.interes,
                    onValueChange = { viewModel.onEvent(PrestamoEditUiEvent.InteresChanged(it)) },
                    label = { Text("Interés %") },
                    isError = state.errorInteres != null,
                    supportingText = { state.errorInteres?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.cuotas,
                onValueChange = { viewModel.onEvent(PrestamoEditUiEvent.CuotasChanged(it)) },
                label = { Text("Cuotas (semanas)") },
                isError = state.errorCuotas != null,
                supportingText = { state.errorCuotas?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            state.errorCliente?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ResumenLinea("Total a pagar", state.montoTotal)
                    ResumenLinea("Cuota semanal", state.montoCuota)
                }
            }

            Button(
                onClick = { viewModel.onEvent(PrestamoEditUiEvent.Otorgar) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("Otorgar préstamo")
            }
        }
    }
}

@Composable
private fun ClienteSelectItem(cliente: Cliente, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column {
            Text(cliente.nombre, fontWeight = FontWeight.SemiBold)
            Text(
                cliente.cedula,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResumenLinea(label: String, monto: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(
            "RD$${"%,.0f".format(monto)}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}