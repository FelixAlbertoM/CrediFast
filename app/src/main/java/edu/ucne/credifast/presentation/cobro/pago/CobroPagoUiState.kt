package edu.ucne.credifast.presentation.cobro.pago

data class CobroPagoUiState(
    val cuotaId: Int = 0,
    val nombreCliente: String = "",
    val numeroCuota: Int = 0,
    val totalCuotas: Int = 0,
    val montoCuota: Double = 0.0,
    val balancePendiente: Double = 0.0,
    val montoIngresado: String = "",
    val nota: String = "",
    val isLoading: Boolean = false,
    val pagoRealizadoId: Int? = null,
    val errorMensaje: String? = null
)