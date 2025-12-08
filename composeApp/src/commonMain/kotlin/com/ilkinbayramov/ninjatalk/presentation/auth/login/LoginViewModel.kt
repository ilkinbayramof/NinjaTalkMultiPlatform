package com.ilkinbayramov.ninjatalk.presentation.auth.login

import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.ninjatalk.core.mvi.MviViewModel
import com.ilkinbayramov.ninjatalk.data.TokenManager
import com.ilkinbayramov.ninjatalk.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) :
        MviViewModel<LoginUiEvent, LoginUiState, LoginUiEffect>(initialState = LoginUiState()) {

    private val tokenManager = TokenManager()

    override fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged ->
                    setState { copy(email = event.value, emailError = null) }
            is LoginUiEvent.PasswordChanged ->
                    setState { copy(password = event.value, passwordError = null) }
            LoginUiEvent.TogglePasswordVisibility ->
                    setState { copy(isPasswordVisible = !isPasswordVisible) }
            LoginUiEvent.LoginClick -> validateAndLogin()
            LoginUiEvent.RegisterClick -> sendEffect { LoginUiEffect.NavigateToRegister }
        }
    }

    private fun validateAndLogin() {
        val state = uiState.value
        var hasError = false

        if (state.email.isBlank()) {
            setState { copy(emailError = "E-posta zorunludur") }
            hasError = true
        }

        if (state.password.length < 6) {
            setState { copy(passwordError = "Şifre en az 6 karakter olmalıdır") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            authRepository
                    .login(email = state.email, password = state.password)
                    .onSuccess { response ->
                        tokenManager.saveToken(response.token)
                        com.ilkinbayramov.ninjatalk.utils.TokenManager.setUserId(response.userId)
                        setState { copy(isLoading = false) }
                        sendEffect { LoginUiEffect.NavigateToHome }
                    }
                    .onFailure { error ->
                        setState { copy(isLoading = false) }
                        sendEffect {
                            LoginUiEffect.ShowErrorMessage(error.message ?: "Giriş başarısız oldu")
                        }
                    }
        }
    }
}
