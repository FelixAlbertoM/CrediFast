package edu.ucne.credifast.presentation.auth

import edu.ucne.credifast.domain.auth.model.UsuarioAuth

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val usuario: UsuarioAuth? = null,
    val errorMessage: String? = null
)