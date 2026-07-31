package edu.ucne.credifast.cliente

import com.google.common.truth.Truth.assertThat
import edu.ucne.credifast.data.cliente.ClienteRepositoryImpl
import edu.ucne.credifast.data.local.dao.ClienteDao
import edu.ucne.credifast.data.local.entities.ClienteEntity
import edu.ucne.credifast.domain.cliente.model.Cliente
import edu.ucne.credifast.domain.common.Resource
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ClienteRepositoryImplTest {

    private lateinit var dao: ClienteDao
    private lateinit var repository: ClienteRepositoryImpl

    private val clienteNuevo = Cliente(
        clienteId = 0,
        nombre = "María Peña",
        cedula = "40225871634",
        telefono = "8095552314",
        direccion = "C/ Sánchez #8"
    )

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = ClienteRepositoryImpl(dao)
    }

    @Test
    fun `guardar cliente valido devuelve Success`() = runTest {
        coEvery { dao.getByCedula(any()) } returns null
        coEvery { dao.getByTelefono(any()) } returns null
        coJustRun { dao.upsert(any()) }

        val resultado = repository.guardarCliente(clienteNuevo)

        assertThat(resultado).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `guardar cliente con cedula duplicada devuelve Error`() = runTest {
        coEvery { dao.getByCedula("40225871634") } returns
                ClienteEntity(
                    clienteId = 99, nombre = "Otro", cedula = "40225871634",
                    telefono = "8290000000", direccion = "X"
                )

        val resultado = repository.guardarCliente(clienteNuevo)

        assertThat(resultado).isInstanceOf(Resource.Error::class.java)
        assertThat((resultado as Resource.Error).message).contains("cédula")
    }

    @Test
    fun `guardar cliente con telefono duplicado devuelve Error`() = runTest {
        coEvery { dao.getByCedula(any()) } returns null
        coEvery { dao.getByTelefono("8095552314") } returns
                ClienteEntity(
                    clienteId = 77, nombre = "Otro", cedula = "00000000000",
                    telefono = "8095552314", direccion = "X"
                )

        val resultado = repository.guardarCliente(clienteNuevo)

        assertThat(resultado).isInstanceOf(Resource.Error::class.java)
        assertThat((resultado as Resource.Error).message).contains("teléfono")
    }

    @Test
    fun `editar el mismo cliente no lo cuenta como duplicado`() = runTest {
        val clienteEditado = clienteNuevo.copy(clienteId = 5)
        coEvery { dao.getByCedula("40225871634") } returns
                ClienteEntity(
                    clienteId = 5, nombre = "María Peña", cedula = "40225871634",
                    telefono = "8095552314", direccion = "C/ Sánchez #8"
                )
        coEvery { dao.getByTelefono("8095552314") } returns
                ClienteEntity(
                    clienteId = 5, nombre = "María Peña", cedula = "40225871634",
                    telefono = "8095552314", direccion = "C/ Sánchez #8"
                )
        coJustRun { dao.upsert(any()) }

        val resultado = repository.guardarCliente(clienteEditado)

        assertThat(resultado).isInstanceOf(Resource.Success::class.java)
    }
}