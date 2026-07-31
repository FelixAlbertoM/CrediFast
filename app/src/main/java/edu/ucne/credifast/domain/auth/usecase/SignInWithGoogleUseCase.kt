package edu.ucne.credifast.domain.auth.usecase

import edu.ucne.credifast.domain.auth.model.UsuarioAuth
import edu.ucne.credifast.domain.auth.repository.AuthRepository
import edu.ucne.credifast.domain.common.Resource
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Resource<UsuarioAuth> = repository.signInWithGoogle()
}