package edu.ucne.credifast.domain.cliente.usecase

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import edu.ucne.credifast.domain.common.Resource
import javax.inject.Inject

class GuardarClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(cliente: Cliente): Resource<Unit> {
        if (cliente.nombre.isBlank()) {
            return Resource.Error("El nombre es obligatorio")
        }
        if (cliente.cedula.length != 11 || !cliente.cedula.all { it.isDigit() }) {
            return Resource.Error("La cédula debe tener 11 dígitos numéricos")
        }
        if (cliente.telefono.length != 10 || !cliente.telefono.all { it.isDigit() }) {
            return Resource.Error("El teléfono debe tener 10 dígitos numéricos")
        }
        if (cliente.direccion.isBlank()) {
            return Resource.Error("La dirección es obligatoria")
        }
        return repository.guardarCliente(cliente)
    }
}