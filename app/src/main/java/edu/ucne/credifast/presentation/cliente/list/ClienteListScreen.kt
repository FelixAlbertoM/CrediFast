package edu.ucne.credifast.presentation.cliente.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.FilterChip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteListScreen(
    modifier: Modifier = Modifier,
    onClienteClick: (Int) -> Unit,
    viewModel: ClienteListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Clientes") }) },

        ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = state.filtro,
                onValueChange = { viewModel.onEvent(ClienteListUiEvent.FiltroChanged(it)) },
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por nombre, cédula o teléfono") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.chipSeleccionado == FiltroCliente.TODOS,
                    onClick = { viewModel.onEvent(ClienteListUiEvent.ChipChanged(FiltroCliente.TODOS)) },
                    label = { Text("Todos ${state.totalClientes}") }
                )
                FilterChip(
                    selected = state.chipSeleccionado == FiltroCliente.CON_PRESTAMO,
                    onClick = { viewModel.onEvent(ClienteListUiEvent.ChipChanged(FiltroCliente.CON_PRESTAMO)) },
                    label = { Text("Con préstamo") }
                )
                FilterChip(
                    selected = state.chipSeleccionado == FiltroCliente.LISTA_NEGRA,
                    onClick = { viewModel.onEvent(ClienteListUiEvent.ChipChanged(FiltroCliente.LISTA_NEGRA)) },
                    label = { Text("Lista negra") }
                )
            }

            if (state.clientesFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay clientes en esta categoría",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.clientesFiltrados, key = { it.cliente.clienteId }) { item ->
                        ClienteItem(item = item, onClick = { onClienteClick(item.cliente.clienteId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ClienteItem(item: ClienteListItem, onClick: () -> Unit) {
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
                        item.cliente.nombre.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(item.cliente.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    "${item.cliente.cedula} · ${item.cliente.telefono}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            EstadoBadge(item.estado)
        }
    }
}

@Composable
private fun EstadoBadge(estado: EstadoCliente) {
    val (texto, fondo, textoColor) = when (estado) {
        EstadoCliente.AL_DIA -> Triple("Al día", Color(0xFFD3F1E2), Color(0xFF0E6B4F))
        EstadoCliente.EN_MORA -> Triple("Mora", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
        EstadoCliente.SIN_PRESTAMO -> Triple("Sin préstamo", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        EstadoCliente.LISTA_NEGRA -> Triple("Lista negra", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
    }

    Surface(shape = MaterialTheme.shapes.small, color = fondo) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textoColor
        )
    }
}