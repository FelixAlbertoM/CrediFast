package edu.ucne.credifast.presentation.cobro.pago

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CobroPagoScreen(
    cuotaId: Int,
    onBack: () -> Unit,
    onPagoRealizado: (Int) -> Unit,
    viewModel: CobroPagoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cuotaId) { viewModel.cargar(cuotaId) }

    LaunchedEffect(state.pagoRealizadoId) {
        state.pagoRealizadoId?.let { onPagoRealizado(it) }
    }
    LaunchedEffect(state.errorMensaje) {
        state.errorMensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(CobroPagoUiEvent.MensajeMostrado)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Realizar cobro") },
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
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        state.nombreCliente,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Cuota ${state.numeroCuota} de ${state.totalCuotas}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.onEvent(CobroPagoUiEvent.PagarCuotaCompleta) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cuota completa")
                        Text("RD$${"%,.0f".format(state.montoCuota)}", fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedButton(
                    onClick = { viewModel.onEvent(CobroPagoUiEvent.SaldarPrestamo) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Saldar préstamo")
                        Text("RD$${"%,.0f".format(state.balancePendiente)}", fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedTextField(
                value = state.montoIngresado,
                onValueChange = { viewModel.onEvent(CobroPagoUiEvent.MontoChanged(it)) },
                label = { Text("Monto a pagar (RD$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.nota,
                onValueChange = { viewModel.onEvent(CobroPagoUiEvent.NotaChanged(it)) },
                label = { Text("Nota (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.onEvent(CobroPagoUiEvent.PagarMontoLibre) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.montoIngresado.isNotBlank()
            ) {
                Text("Realizar pago y generar recibo")
            }
        }
    }
}