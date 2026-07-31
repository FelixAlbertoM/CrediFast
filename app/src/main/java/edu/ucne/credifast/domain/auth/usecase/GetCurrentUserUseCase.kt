package edu.ucne.credifast.domain.auth.usecase

import edu.ucne.credifast.domain.auth.model.UsuarioAuth
import edu.ucne.credifast.domain.auth.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): UsuarioAuth? = repository.getCurrentUser()
    fun isLoggedIn(): Boolean = repository.isLoggedIn()
}