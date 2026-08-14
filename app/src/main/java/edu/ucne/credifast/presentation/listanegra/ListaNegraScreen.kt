package edu.ucne.credifast.presentation.listanegra

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import edu.ucne.credifast.domain.cliente.model.Cliente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaNegraScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: ListaNegraViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var clienteAQuitar by remember { mutableStateOf<Cliente?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Lista negra") },
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
                onValueChange = { viewModel.onEvent(ListaNegraUiEvent.FiltroChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar en lista negra") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Text(
                "Los clientes en lista negra permanecen en el sistema pero no pueden recibir préstamos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (state.clientesFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay clientes en lista negra",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.clientesFiltrados, key = { it.clienteId }) { cliente ->
                        ListaNegraItem(cliente = cliente, onQuitar = { clienteAQuitar = cliente })
                    }
                }
            }
        }
    }

    clienteAQuitar?.let { cliente ->
        AlertDialog(
            onDismissRequest = { clienteAQuitar = null },
            title = { Text("Quitar de lista negra") },
            text = { Text("¿Deseas quitar a ${cliente.nombre} de la lista negra? Podrá recibir préstamos de nuevo.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(ListaNegraUiEvent.QuitarDeListaNegra(cliente))
                    clienteAQuitar = null
                }) {
                    Text("Quitar")
                }
            },
            dismissButton = {
                TextButton(onClick = { clienteAQuitar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ListaNegraItem(cliente: Cliente, onQuitar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onQuitar,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        cliente.nombre.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(cliente.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    "${cliente.cedula} · ${cliente.telefono}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Razón: ${cliente.razonListaNegra ?: "sin especificar"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}