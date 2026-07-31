package edu.ucne.credifast.data.cliente

import edu.ucne.credifast.data.local.dao.ClienteDao
import edu.ucne.credifast.data.mapper.toDomain
import edu.ucne.credifast.data.mapper.toEntity
import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.cliente.repository.ClienteRepository
import edu.ucne.credifast.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClienteRepositoryImpl @Inject constructor(
    private val clienteDao: ClienteDao
) : ClienteRepository {

    override fun observeClientes(): Flow<List<Cliente>> =
        clienteDao.observeAll().map { lista -> lista.map { it.toDomain() } }

    override suspend fun getCliente(id: Int): Cliente? =
        clienteDao.getById(id)?.toDomain()

    override suspend fun guardarCliente(cliente: Cliente): Resource<Unit> {
        if (existeCedula(cliente.cedula, cliente.clienteId)) {
            return Resource.Error("Ya existe un cliente con esa cédula")
        }
        if (existeTelefono(cliente.telefono, cliente.clienteId)) {
            return Resource.Error("Ya existe un cliente con ese teléfono")
        }
        return try {
            clienteDao.upsert(cliente.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "No se pudo guardar el cliente")
        }
    }

    override suspend fun eliminarCliente(cliente: Cliente): Resource<Unit> {
        return try {
            clienteDao.delete(cliente.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "No se pudo eliminar el cliente")
        }
    }

    override suspend fun existeCedula(cedula: String, excluirId: Int): Boolean {
        val encontrado = clienteDao.getByCedula(cedula) ?: return false
        return encontrado.clienteId != excluirId
    }

    override suspend fun existeTelefono(telefono: String, excluirId: Int): Boolean {
        val encontrado = clienteDao.getByTelefono(telefono) ?: return false
        return encontrado.clienteId != excluirId
    }
}