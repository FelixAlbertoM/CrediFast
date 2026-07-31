package edu.ucne.credifast.data.mapper

import edu.ucne.credifast.data.local.entities.ClienteEntity
import edu.ucne.credifast.domain.cliente.model.Cliente

fun ClienteEntity.toDomain(): Cliente = Cliente(
    clienteId = clienteId,
    nombre = nombre,
    cedula = cedula,
    telefono = telefono,
    direccion = direccion,
    enListaNegra = enListaNegra,
    razonListaNegra = razonListaNegra,
    fechaListaNegra = fechaListaNegra,
    fechaRegistro = fechaRegistro
)

fun Cliente.toEntity(): ClienteEntity = ClienteEntity(
    clienteId = clienteId,
    nombre = nombre,
    cedula = cedula,
    telefono = telefono,
    direccion = direccion,
    enListaNegra = enListaNegra,
    razonListaNegra = razonListaNegra,
    fechaListaNegra = fechaListaNegra,
    fechaRegistro = fechaRegistro
)