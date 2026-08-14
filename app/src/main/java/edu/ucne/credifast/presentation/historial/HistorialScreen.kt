package edu.ucne.credifast.presentation.historial

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.credifast.domain.historial.model.PrestamoHistorial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onPrestamoClick: (Int) -> Unit,
    viewModel: HistorialViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var mostrarDatePicker by remember { mutableStateOf(false) }

    val formatoFechaChip = SimpleDateFormat("d MMM yyyy", Locale("es"))

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = state.filtro,
                onValueChange = { viewModel.onEvent(HistorialUiEvent.FiltroChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por nombre, cédula o teléfono") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.orden == OrdenHistorial.RECIENTES,
                    onClick = { viewModel.onEvent(HistorialUiEvent.OrdenChanged(OrdenHistorial.RECIENTES)) },
                    label = { Text("Recientes primero") }
                )
                FilterChip(
                    selected = state.orden == OrdenHistorial.ANTIGUOS,
                    onClick = { viewModel.onEvent(HistorialUiEvent.OrdenChanged(OrdenHistorial.ANTIGUOS)) },
                    label = { Text("Más antiguos") }
                )
                FilterChip(
                    selected = state.orden == OrdenHistorial.POR_FECHA,
                    onClick = {
                        viewModel.onEvent(HistorialUiEvent.OrdenChanged(OrdenHistorial.POR_FECHA))
                        mostrarDatePicker = true
                    },
                    label = {
                        Text(
                            if (state.orden == OrdenHistorial.POR_FECHA && state.fechaSeleccionada != null)
                                formatoFechaChip.format(Date(state.fechaSeleccionada!!))
                            else "Por fecha"
                        )
                    }
                )
            }

            if (state.prestamosFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay préstamos saldados aún",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.prestamosFiltrados, key = { it.prestamoId }) { p ->
                        HistorialItem(prestamo = p, onClick = { onPrestamoClick(p.prestamoId) })
                    }
                }
            }
        }
    }

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.fechaSeleccionada
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(HistorialUiEvent.FechaSeleccionada(datePickerState.selectedDateMillis))
                    mostrarDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun HistorialItem(prestamo: PrestamoHistorial, onClick: () -> Unit) {
    val formatoFecha = SimpleDateFormat("d MMM yyyy", Locale("es"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
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
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        prestamo.nombreCliente.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(prestamo.nombreCliente, fontWeight = FontWeight.SemiBold)
                Text(
                    "Otorgado ${formatoFecha.format(Date(prestamo.fechaOtorgado))} · RD$${"%,.0f".format(prestamo.capital)} al ${prestamo.interesPorcentaje.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    "Saldado",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}