package edu.ucne.credifast.domain.cliente.repository

import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface ClienteRepository {
    fun observeClientes(): Flow<List<Cliente>>
    suspend fun getCliente(id: Int): Cliente?
    suspend fun guardarCliente(cliente: Cliente): Resource<Unit>
    suspend fun eliminarCliente(cliente: Cliente): Resource<Unit>
    suspend fun existeCedula(cedula: String, excluirId: Int): Boolean
    suspend fun existeTelefono(telefono: String, excluirId: Int): Boolean
    fun observeListaNegra(): Flow<List<Cliente>>
}