package edu.ucne.credifast.domain.auth.repository

import edu.ucne.credifast.domain.auth.model.UsuarioAuth
import edu.ucne.credifast.domain.common.Resource

interface AuthRepository {
    suspend fun signInWithGoogle(): Resource<UsuarioAuth>
    fun signOut()
    fun getCurrentUser(): UsuarioAuth?
    fun isLoggedIn(): Boolean
    fun observeAuthState(): kotlinx.coroutines.flow.Flow<Boolean>
}