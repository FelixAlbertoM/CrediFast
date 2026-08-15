package edu.ucne.credifast.presentation.cobro.list

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
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
import edu.ucne.credifast.domain.cobro.usecase.RangoDia
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CobrosScreen(
    modifier: Modifier = Modifier,
    onCobrarCuota: (Int) -> Unit,
    viewModel: CobrosViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Cobros") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Calendario(
                diaSeleccionado = state.diaSeleccionado,
                onDiaClick = { viewModel.onEvent(CobrosUiEvent.DiaSeleccionado(it)) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TotalCard(
                    titulo = "Por recolectar",
                    monto = state.totalPorCobrar,
                    contenedor = MaterialTheme.colorScheme.surfaceVariant,
                    contenido = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                TotalCard(
                    titulo = "Cobrado",
                    monto = state.totalCobrado,
                    contenedor = MaterialTheme.colorScheme.primaryContainer,
                    contenido = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            if (state.cobros.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay cuotas para cobrar este día",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.cobros, key = { it.cuota.cuotaId }) { item ->
                        CobroItemCard(item = item, onClick = { onCobrarCuota(item.cuota.cuotaId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun Calendario(diaSeleccionado: Long, onDiaClick: (Long) -> Unit) {
    var semanaBase by remember { mutableStateOf(0) }
    val hoy = remember { System.currentTimeMillis() }

    val diaAncla = RangoDia.sumarDias(hoy, semanaBase * 7)
    val dias = (0..6).map { RangoDia.sumarDias(RangoDia.inicioDe(diaAncla), it - 3) }

    val formatoMes = SimpleDateFormat("MMMM yyyy", Locale("es"))
    val formatoDiaSemana = SimpleDateFormat("EEE", Locale("es"))
    val formatoNumero = SimpleDateFormat("d", Locale("es"))
    val inicioSeleccionado = RangoDia.inicioDe(diaSeleccionado)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { semanaBase-- }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Semana anterior")
            }
            Text(
                formatoMes.format(Date(diaAncla)).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = { semanaBase++ }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Semana siguiente")
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dias) { dia ->
                val seleccionado = RangoDia.inicioDe(dia) == inicioSeleccionado
                val esHoy = RangoDia.inicioDe(dia) == RangoDia.inicioDe(hoy)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (seleccionado) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface,
                    border = if (seleccionado) null
                    else androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (esHoy) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.size(width = 56.dp, height = 64.dp),
                    onClick = { onDiaClick(dia) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            formatoDiaSemana.format(Date(dia)).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (seleccionado) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            formatoNumero.format(Date(dia)),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (seleccionado) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalCard(
    titulo: String,
    monto: Double,
    contenedor: androidx.compose.ui.graphics.Color,
    contenido: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = contenedor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(titulo.uppercase(), style = MaterialTheme.typography.labelSmall, color = contenido)
            Text(
                "RD$${"%,.0f".format(monto)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contenido
            )
        }
    }
}

@Composable
private fun CobroItemCard(item: CobroItem, onClick: () -> Unit) {
    val pagada = item.cuota.estaPagada
    val vencida = !pagada && item.cuota.diasAtraso() > 0

    val (estadoTexto, estadoColor, estadoFondo) = when {
        pagada -> Triple("Cobrada", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
        vencida -> Triple("Vencida", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        else -> Triple("Pendiente", MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }
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
                color = estadoFondo
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (pagada) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = estadoColor
                        )
                    } else {
                        Text(
                            item.nombreCliente.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = estadoColor
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombreCliente, fontWeight = FontWeight.SemiBold)
                Text(
                    "Cuota ${item.numeroCuota} de ${item.totalCuotas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("RD$${"%,d".format(item.totalCuota)}", fontWeight = FontWeight.Bold)
                Surface(shape = MaterialTheme.shapes.small, color = estadoFondo) {
                    Text(
                        estadoTexto,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = estadoColor
                    )
                }
            }
        }
    }
}