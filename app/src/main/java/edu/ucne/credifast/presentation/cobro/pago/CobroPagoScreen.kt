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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CobroPagoScreen(
    cuotaId: Int,
    onBack: () -> Unit,
    onPagoRealizado: (Int) -> Unit,
    viewModel: CobroPagoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var accionAConfirmar by remember { mutableStateOf<CobroPagoUiEvent?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cuotaId) { viewModel.cargar(cuotaId) }

    LaunchedEffect(state.pagoRealizadoId) {
        state.pagoRealizadoId?.let {
            onPagoRealizado(it)
            viewModel.limpiarNavegacion()
        }
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                state.nombreCliente.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
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
                    Text(
                        "RD$${"%,.0f".format(state.montoCuota)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { accionAConfirmar = CobroPagoUiEvent.PagarCuotaCompleta },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cuota completa")
                        Text("RD$${"%,.0f".format(state.montoCuota)}", fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedButton(
                    onClick = { accionAConfirmar = CobroPagoUiEvent.SaldarPrestamo },
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
                placeholder = { Text("Ej.: abonó de más, pagó su esposa…") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { accionAConfirmar = CobroPagoUiEvent.PagarMontoLibre },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.montoIngresado.isNotBlank()
            ) {
                Text("Realizar pago y generar recibo")
            }
        }
    }
    accionAConfirmar?.let { accion ->
        val (titulo, mensaje) = when (accion) {
            CobroPagoUiEvent.PagarCuotaCompleta ->
                "Cobrar cuota completa" to "¿Confirmas el cobro de la cuota completa por RD$${"%,.0f".format(state.montoCuota)} a ${state.nombreCliente}?"
            CobroPagoUiEvent.SaldarPrestamo ->
                "Saldar préstamo" to "¿Confirmas saldar el préstamo completo por RD$${"%,.0f".format(state.balancePendiente)} de ${state.nombreCliente}?"
            else ->
                "Realizar pago" to "¿Confirmas el pago de RD$${state.montoIngresado} de ${state.nombreCliente}?"
        }
        AlertDialog(
            onDismissRequest = { accionAConfirmar = null },
            title = { Text(titulo) },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(accion)
                    accionAConfirmar = null
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { accionAConfirmar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}