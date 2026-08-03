package edu.ucne.credifast.data.mapper

import edu.ucne.credifast.data.local.entities.CuotaEntity
import edu.ucne.credifast.data.local.entities.PrestamoEntity
import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.model.Prestamo

fun PrestamoEntity.toDomain(): Prestamo = Prestamo(
    prestamoId = prestamoId,
    clienteId = clienteId,
    capital = capital,
    interesPorcentaje = interesPorcentaje,
    cantidadCuotas = cantidadCuotas,
    montoTotal = montoTotal,
    montoCuota = montoCuota,
    balancePendiente = balancePendiente,
    estado = estado,
    fechaOtorgado = fechaOtorgado
)

fun Prestamo.toEntity(): PrestamoEntity = PrestamoEntity(
    prestamoId = prestamoId,
    clienteId = clienteId,
    capital = capital,
    interesPorcentaje = interesPorcentaje,
    cantidadCuotas = cantidadCuotas,
    montoTotal = montoTotal,
    montoCuota = montoCuota,
    balancePendiente = balancePendiente,
    estado = estado,
    fechaOtorgado = fechaOtorgado
)

fun CuotaEntity.toDomain(): Cuota = Cuota(
    cuotaId = cuotaId,
    prestamoId = prestamoId,
    numeroCuota = numeroCuota,
    fechaVencimiento = fechaVencimiento,
    monto = monto,
    montoPagado = montoPagado,
    estado = estado,
    fechaPago = fechaPago
)

fun Cuota.toEntity(): CuotaEntity = CuotaEntity(
    cuotaId = cuotaId,
    prestamoId = prestamoId,
    numeroCuota = numeroCuota,
    fechaVencimiento = fechaVencimiento,
    monto = monto,
    montoPagado = montoPagado,
    estado = estado,
    fechaPago = fechaPago
)