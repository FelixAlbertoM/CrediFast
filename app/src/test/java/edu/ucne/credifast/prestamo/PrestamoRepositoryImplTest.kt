package edu.ucne.credifast.prestamo

import com.google.common.truth.Truth.assertThat
import edu.ucne.credifast.data.local.dao.ClienteDao
import edu.ucne.credifast.data.local.dao.CuotaDao
import edu.ucne.credifast.data.local.dao.PrestamoDao
import edu.ucne.credifast.data.local.entities.ClienteEntity
import edu.ucne.credifast.data.local.entities.PrestamoEntity
import edu.ucne.credifast.data.prestamo.PrestamoRepositoryImpl
import edu.ucne.credifast.domain.common.Resource
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PrestamoRepositoryImplTest {

    private lateinit var prestamoDao: PrestamoDao
    private lateinit var cuotaDao: CuotaDao
    private lateinit var clienteDao: ClienteDao
    private lateinit var repository: PrestamoRepositoryImpl

    private val clienteNormal = ClienteEntity(
        clienteId = 1, nombre = "María Peña", cedula = "40225871634",
        telefono = "8095552314", direccion = "C/ Sánchez #8"
    )

    @Before
    fun setUp() {
        prestamoDao = mockk(relaxed = true)
        cuotaDao = mockk(relaxed = true)
        clienteDao = mockk(relaxed = true)
        repository = PrestamoRepositoryImpl(prestamoDao, cuotaDao, clienteDao)
    }

    @Test
    fun `otorgar prestamo valido devuelve Success y genera cuotas`() = runTest {
        coEvery { clienteDao.getById(1) } returns clienteNormal
        coEvery { prestamoDao.getPrestamoActivoDeCliente(1) } returns null
        coEvery { prestamoDao.upsert(any()) } returns 10L
        coJustRun { cuotaDao.upsertAll(any()) }

        val resultado = repository.otorgarPrestamo(
            clienteId = 1, capital = 10000.0, interesPorcentaje = 20.0, cantidadCuotas = 10
        )

        assertThat(resultado).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `otorgar a cliente en lista negra devuelve Error`() = runTest {
        coEvery { clienteDao.getById(1) } returns clienteNormal.copy(enListaNegra = true)

        val resultado = repository.otorgarPrestamo(
            clienteId = 1, capital = 10000.0, interesPorcentaje = 20.0, cantidadCuotas = 10
        )

        assertThat(resultado).isInstanceOf(Resource.Error::class.java)
        assertThat((resultado as Resource.Error).message).contains("lista negra")
    }

    @Test
    fun `otorgar a cliente con prestamo activo devuelve Error`() = runTest {
        coEvery { clienteDao.getById(1) } returns clienteNormal
        coEvery { prestamoDao.getPrestamoActivoDeCliente(1) } returns
                PrestamoEntity(
                    prestamoId = 5, clienteId = 1, capital = 5000.0, interesPorcentaje = 20.0,
                    cantidadCuotas = 5, montoTotal = 6000.0, montoCuota = 1200.0,
                    balancePendiente = 3600.0, estado = "ACTIVO"
                )

        val resultado = repository.otorgarPrestamo(
            clienteId = 1, capital = 10000.0, interesPorcentaje = 20.0, cantidadCuotas = 10
        )

        assertThat(resultado).isInstanceOf(Resource.Error::class.java)
        assertThat((resultado as Resource.Error).message).contains("activo")
    }

    @Test
    fun `otorgar con monto cero devuelve Error`() = runTest {
        val resultado = repository.otorgarPrestamo(
            clienteId = 1, capital = 0.0, interesPorcentaje = 20.0, cantidadCuotas = 10
        )

        assertThat(resultado).isInstanceOf(Resource.Error::class.java)
    }
}