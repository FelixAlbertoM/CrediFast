package edu.ucne.credifast.presentation.auth

sealed interface LoginUiEvent {
    data object SignInWithGoogle : LoginUiEvent
    data object SignOut : LoginUiEvent
    data object ClearError : LoginUiEvent
}