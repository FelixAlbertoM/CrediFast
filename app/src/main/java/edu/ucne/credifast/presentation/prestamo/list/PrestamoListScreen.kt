package edu.ucne.credifast.presentation.prestamo.list

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import java.util.Date
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import java.util.Locale
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import androidx.compose.material3.FilterChip
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoListScreen(
    modifier: Modifier = Modifier,
    onPrestamoClick: (Int) -> Unit,
    viewModel: PrestamoListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Préstamos") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = state.filtro,
                onValueChange = { viewModel.onEvent(PrestamoListUiEvent.FiltroChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por cliente, cédula o teléfono") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.chipSeleccionado == FiltroPrestamo.ACTIVOS,
                    onClick = { viewModel.onEvent(PrestamoListUiEvent.ChipChanged(FiltroPrestamo.ACTIVOS)) },
                    label = { Text("Activos") }
                )
                FilterChip(
                    selected = state.chipSeleccionado == FiltroPrestamo.SALDADOS,
                    onClick = { viewModel.onEvent(PrestamoListUiEvent.ChipChanged(FiltroPrestamo.SALDADOS)) },
                    label = { Text("Saldados") }
                )
                FilterChip(
                    selected = state.chipSeleccionado == FiltroPrestamo.EN_MORA,
                    onClick = { viewModel.onEvent(PrestamoListUiEvent.ChipChanged(FiltroPrestamo.EN_MORA)) },
                    label = { Text("En mora") }
                )
            }

            if (state.prestamosFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay préstamos en esta categoría",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.prestamosFiltrados, key = { it.prestamoId }) { item ->
                        PrestamoItem(item = item, onClick = { onPrestamoClick(item.prestamoId) })
                    }

                    if (state.totalEnLaCalle > 0) {
                        item {
                            Text(
                                "Total en la calle: RD$${"%,.0f".format(state.totalEnLaCalle)}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrestamoItem(item: PrestamoListItem, onClick: () -> Unit) {
    val formatoFecha = SimpleDateFormat("EEE d MMM", Locale.forLanguageTag("es"))
    val enMora = item.cuotasVencidas > 0

    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = if (enMora) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        item.nombreCliente.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = if (enMora) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombreCliente, fontWeight = FontWeight.SemiBold)
                if (enMora) {
                    Text(
                        "${item.cuotasVencidas} cuota(s) vencida(s) · mora RD$${"%,.0f".format(item.moraAcumulada)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    val venceTexto = item.fechaProximoVencimiento?.let {
                        "vence ${formatoFecha.format(Date(it))}"
                    } ?: "sin cuotas pendientes"
                    Text(
                        "Cuota ${item.numeroCuotaActual} de ${item.totalCuotas} · $venceTexto",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "RD$${"%,.0f".format(item.balancePendiente)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "restante",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}