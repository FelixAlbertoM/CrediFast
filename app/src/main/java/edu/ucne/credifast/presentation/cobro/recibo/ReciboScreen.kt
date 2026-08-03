package edu.ucne.credifast.presentation.cobro.recibo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.Row

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciboScreen(
    pagoId: Int,
    onCerrar: () -> Unit,
    viewModel: ReciboViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(pagoId) { viewModel.cargar(pagoId) }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Recibo de pago") },
                navigationIcon = {
                    IconButton(onClick = onCerrar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { padding ->
        val recibo = state.recibo
        if (recibo == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(if (state.isLoading) "Generando recibo…" else "No se encontró el recibo")
            }
            return@Scaffold
        }

        val formatoFecha = SimpleDateFormat("d MMM yyyy · h:mm a", Locale("es"))
        val formatoVenc = SimpleDateFormat("EEE d MMM yyyy", Locale("es"))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        recibo.nombreEmpresa,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Recibo de pago",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                    LineaRecibo("Fecha y hora", formatoFecha.format(Date(recibo.fechaPago)))
                    LineaRecibo("Cliente", recibo.nombreCliente)
                    LineaRecibo("Cédula", recibo.cedulaCliente)
                    LineaRecibo("Cuota", "${recibo.numeroCuota} de ${recibo.totalCuotas}")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                    LineaRecibo(
                        "Monto cobrado",
                        "RD$${"%,.2f".format(recibo.montoCobrado)}",
                        destacado = true
                    )
                    LineaRecibo("Capital", "RD$${"%,.2f".format(recibo.capital)}")
                    LineaRecibo("Interés", "RD$${"%,.2f".format(recibo.interes)}")
                    LineaRecibo("Balance restante", "RD$${"%,.2f".format(recibo.balanceRestante)}")
                    LineaRecibo(
                        "Próximo vencimiento",
                        recibo.proximoVencimiento?.let { formatoVenc.format(Date(it)) } ?: "Préstamo saldado"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                    Text(
                        "¡Gracias por su pago!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                Text("Listo")
            }
        }
    }
}

@Composable
private fun LineaRecibo(label: String, valor: String, destacado: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            valor,
            style = if (destacado) MaterialTheme.typography.titleMedium
            else MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.SemiBold,
            color = if (destacado) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}