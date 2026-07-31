package edu.ucne.credifast.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.credifast.domain.auth.usecase.GetCurrentUserUseCase
import edu.ucne.credifast.domain.auth.usecase.SignInWithGoogleUseCase
import edu.ucne.credifast.domain.auth.usecase.SignOutUseCase
import edu.ucne.credifast.domain.common.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginUiState(
            isLoggedIn = getCurrentUserUseCase.isLoggedIn(),
            usuario = getCurrentUserUseCase()
        )
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.SignInWithGoogle -> signIn()
            LoginUiEvent.SignOut -> signOut()
            LoginUiEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = signInWithGoogleUseCase()) {
                is Resource.Success -> _state.update {
                    it.copy(isLoading = false, isLoggedIn = true, usuario = result.data)
                }
                is Resource.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                Resource.Loading -> _state.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun signOut() {
        signOutUseCase()
        _state.update { LoginUiState(isLoggedIn = false) }
    }
}