package com.ilkinbayramov.ninjatalk.presentation.auth.login

data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val isPasswordVisible: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val isLoading: Boolean = false
)

sealed interface LoginUiEvent {
    data class EmailChanged(val value: String) : LoginUiEvent
    data class PasswordChanged(val value: String) : LoginUiEvent
    data object TogglePasswordVisibility : LoginUiEvent
    data object LoginClick : LoginUiEvent
    data object RegisterClick : LoginUiEvent
}

sealed interface LoginUiEffect {
    data class ShowErrorMessage(val message: String) : LoginUiEffect
    data object NavigateToHome : LoginUiEffect
    data object NavigateToRegister : LoginUiEffect
}
