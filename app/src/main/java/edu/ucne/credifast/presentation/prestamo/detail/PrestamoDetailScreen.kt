package edu.ucne.credifast.presentation.prestamo.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.model.EstadoCuota
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoDetailScreen(
    prestamoId: Int,
    onBack: () -> Unit,
    onCuotaClick: (Int) -> Unit,
    viewModel: PrestamoDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(prestamoId) { viewModel.cargar(prestamoId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Préstamo #${state.prestamo?.prestamoId ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val prestamo = state.prestamo
        if (prestamo == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Cargando…")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Encabezado
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        state.cliente?.nombre ?: "Cliente",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "RD$${"%,.0f".format(prestamo.balancePendiente)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "restante de RD$${"%,.0f".format(prestamo.montoTotal)} · interés ${prestamo.interesPorcentaje.toInt()}% · ${prestamo.cantidadCuotas} semanas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cuotas · RD$${"%,.0f".format(prestamo.montoCuota)} c/u", fontWeight = FontWeight.SemiBold)
                if (state.moraTotal > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            "Mora RD$${"%,.0f".format(state.moraTotal)}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.cuotas, key = { it.cuotaId }) { cuota ->
                    CuotaItem(cuota = cuota, onClick = { onCuotaClick(cuota.cuotaId) })
                }
            }
        }
    }
}

@Composable
private fun CuotaItem(cuota: Cuota, onClick: () -> Unit) {
    val estado = cuota.estadoActual()
    val (fondo, colorTexto, etiqueta) = when (estado) {
        EstadoCuota.PAGADA -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "Pagada"
        )
        EstadoCuota.RETRASADA -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            "Retrasada"
        )
        EstadoCuota.PENDIENTE -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "Pendiente"
        )
    }

    val formato = SimpleDateFormat("EEE d 'de' MMM", Locale("es"))

    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = fondo
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("${cuota.numeroCuota}", fontWeight = FontWeight.Bold, color = colorTexto)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formato.format(Date(cuota.fechaVencimiento)).replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Medium
                )
                val dias = cuota.diasAtraso()
                if (dias > 0) {
                    Text(
                        "$dias días de atraso · +RD$${"%,.0f".format(cuota.mora())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Surface(shape = RoundedCornerShape(50), color = fondo) {
                Text(
                    etiqueta,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = colorTexto,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}