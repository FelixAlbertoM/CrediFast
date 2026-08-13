package edu.ucne.credifast.domain.dashboard.usecase

import edu.ucne.credifast.domain.cobro.repository.CobroRepository
import edu.ucne.credifast.domain.cobro.usecase.RangoDia
import edu.ucne.credifast.domain.dashboard.model.DashboardData
import edu.ucne.credifast.domain.dashboard.model.RecaudacionDia
import edu.ucne.credifast.domain.mora.usecase.GetClientesEnMoraUseCase
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetDashboardUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val cobroRepository: CobroRepository,
    private val getClientesEnMoraUseCase: GetClientesEnMoraUseCase
) {
    operator fun invoke(): Flow<DashboardData> {
        val ahora = System.currentTimeMillis()
        val inicio7Dias = RangoDia.inicioDe(RangoDia.sumarDias(ahora, -6))
        val fin = RangoDia.finDe(ahora)

        return combine(
            prestamoRepository.observePrestamosPorEstado("ACTIVO"),
            cobroRepository.observePagosEntreFechas(inicio7Dias, fin),
            getClientesEnMoraUseCase()
        ) { activos, pagos7Dias, morosos ->

            val pendiente = activos.sumOf { it.balancePendiente }
            val desembolsado = activos.sumOf { it.capital }
            val recaudado = pagos7Dias.sumOf { it.montoCobrado }

            val formatoDia = SimpleDateFormat("EEE", Locale("es"))
            val recaudacionSemana = (0..6).map { offset ->
                val dia = RangoDia.sumarDias(ahora, -6 + offset)
                val inicioDia = RangoDia.inicioDe(dia)
                val finDia = RangoDia.finDe(dia)
                val montoDia = pagos7Dias
                    .filter { it.fechaPago in inicioDia..finDia }
                    .sumOf { it.montoCobrado }
                RecaudacionDia(
                    etiqueta = formatoDia.format(Date(dia)).replaceFirstChar { it.uppercase() },
                    monto = montoDia,
                    esHoy = offset == 6
                )
            }

            DashboardData(
                pendienteEnCalle = pendiente,
                desembolsado = desembolsado,
                recaudado7Dias = recaudado,
                prestamosActivos = activos.size,
                clientesEnMora = morosos.size,
                recaudacionSemana = recaudacionSemana
            )
        }
    }
}