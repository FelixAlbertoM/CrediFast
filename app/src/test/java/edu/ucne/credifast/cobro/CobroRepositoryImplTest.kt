package edu.ucne.credifast.cobro

import com.google.common.truth.Truth.assertThat
import edu.ucne.credifast.data.cobro.CobroRepositoryImpl
import edu.ucne.credifast.data.local.dao.CuotaDao
import edu.ucne.credifast.data.local.dao.PagoDao
import edu.ucne.credifast.data.local.dao.PrestamoDao
import edu.ucne.credifast.data.local.entities.CuotaEntity
import edu.ucne.credifast.data.local.entities.PrestamoEntity
import edu.ucne.credifast.domain.common.Resource
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CobroRepositoryImplTest {

    private lateinit var cuotaDao: CuotaDao
    private lateinit var prestamoDao: PrestamoDao
    private lateinit var pagoDao: PagoDao
    private lateinit var repository: CobroRepositoryImpl

    private val prestamo = PrestamoEntity(
        prestamoId = 1, clienteId = 1, capital = 10000.0, interesPorcentaje = 20.0,
        cantidadCuotas = 10, montoTotal = 12000.0, montoCuota = 1200.0,
        balancePendiente = 12000.0, estado = "ACTIVO"
    )
    private val cuota = CuotaEntity(
        cuotaId = 1, prestamoId = 1, numeroCuota = 1,
        fechaVencimiento = System.currentTimeMillis(), monto = 1200.0
    )

    @Before
    fun setUp() {
        cuotaDao = mockk(relaxed = true)
        prestamoDao = mockk(relaxed = true)
        pagoDao = mockk(relaxed = true)
        repository = CobroRepositoryImpl(cuotaDao, prestamoDao, pagoDao)
    }

    @Test
    fun `cobrar cuota completa separa capital e interes proporcionalmente`() = runTest {
        coEvery { cuotaDao.getById(1) } returns cuota
        coEvery { prestamoDao.getById(1) } returns prestamo
        coEvery { pagoDao.upsert(any()) } returns 1L

        val resultado = repository.realizarCobro(1, 1200.0, "CUOTA_COMPLETA", null)

        assertThat(resultado).isInstanceOf(Resource.Success::class.java)
        val pago = (resultado as Resource.Success).data
        assertThat(pago.capital).isWithin(0.01).of(1000.0)
        assertThat(pago.interes).isWithin(0.01).of(200.0)
        assertThat(pago.balanceRestante).isWithin(0.01).of(10800.0)
    }

    @Test
    fun `saldar prestamo deja balance en cero`() = runTest {
        coEvery { cuotaDao.getById(1) } returns cuota
        coEvery { prestamoDao.getById(1) } returns prestamo
        coEvery { cuotaDao.getByPrestamo(1) } returns listOf(cuota)
        coEvery { pagoDao.upsert(any()) } returns 1L

        val resultado = repository.realizarCobro(1, 12000.0, "SALDO_TOTAL", null)

        assertThat(resultado).isInstanceOf(Resource.Success::class.java)
        val pago = (resultado as Resource.Success).data
        assertThat(pago.balanceRestante).isWithin(0.01).of(0.0)
    }

    @Test
    fun `cobro con monto cero devuelve Error`() = runTest {
        val resultado = repository.realizarCobro(1, 0.0, "CUOTA_COMPLETA", null)
        assertThat(resultado).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun `cobro de cuota inexistente devuelve Error`() = runTest {
        coEvery { cuotaDao.getById(99) } returns null
        val resultado = repository.realizarCobro(99, 1200.0, "CUOTA_COMPLETA", null)
        assertThat(resultado).isInstanceOf(Resource.Error::class.java)
    }
}