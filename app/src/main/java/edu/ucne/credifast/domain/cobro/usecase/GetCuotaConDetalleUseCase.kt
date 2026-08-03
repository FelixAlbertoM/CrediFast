package edu.ucne.credifast.domain.cobro.usecase

import edu.ucne.credifast.data.local.dao.CuotaDao
import edu.ucne.credifast.data.mapper.toDomain
import edu.ucne.credifast.domain.prestamo.model.Cuota
import edu.ucne.credifast.domain.prestamo.model.Prestamo
import edu.ucne.credifast.domain.prestamo.repository.PrestamoRepository
import javax.inject.Inject

data class CuotaConDetalle(
    val cuota: Cuota,
    val prestamo: Prestamo
)

class GetCuotaConDetalleUseCase @Inject constructor(
    private val cuotaDao: CuotaDao,
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(cuotaId: Int): CuotaConDetalle? {
        val cuota = cuotaDao.getById(cuotaId)?.toDomain() ?: return null
        val prestamo = prestamoRepository.getPrestamo(cuota.prestamoId) ?: return null
        return CuotaConDetalle(cuota, prestamo)
    }
}