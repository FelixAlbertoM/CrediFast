package edu.ucne.credifast.data.mapper

import edu.ucne.credifast.data.local.entities.PagoEntity
import edu.ucne.credifast.domain.cobro.model.Pago

fun PagoEntity.toDomain(): Pago = Pago(
    pagoId = pagoId,
    prestamoId = prestamoId,
    numeroCuota = numeroCuota,
    montoCobrado = montoCobrado,
    capital = capital,
    interes = interes,
    mora = mora,
    balanceRestante = balanceRestante,
    nota = nota,
    tipo = tipo,
    fechaPago = fechaPago
)

fun Pago.toEntity(): PagoEntity = PagoEntity(
    pagoId = pagoId,
    prestamoId = prestamoId,
    numeroCuota = numeroCuota,
    montoCobrado = montoCobrado,
    capital = capital,
    interes = interes,
    mora = mora,
    balanceRestante = balanceRestante,
    nota = nota,
    tipo = tipo,
    fechaPago = fechaPago
)