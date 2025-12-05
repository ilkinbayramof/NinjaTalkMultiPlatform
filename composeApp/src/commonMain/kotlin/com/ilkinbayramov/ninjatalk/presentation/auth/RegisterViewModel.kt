package com.ilkinbayramov.ninjatalk.presentation.auth

import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.ninjatalk.core.mvi.MviViewModel
import com.ilkinbayramov.ninjatalk.data.repository.AuthRepository
import kotlinx.coroutines.launch

import com.ilkinbayramov.ninjatalk.data.TokenManager

class RegisterViewModel(private val authRepository: AuthRepository) :
        MviViewModel<RegisterUiEvent, RegisterUiState, RegisterUiEffect>(
                initialState = RegisterUiState()
        ) {

    private val tokenManager = TokenManager()

    override fun onEvent(event: RegisterUiEvent) {
        when (event) {
            // Email
            is RegisterUiEvent.EmailChanged -> {
                setState { copy(email = event.value, emailError = null) }
            }

            // Password
            is RegisterUiEvent.PasswordChanged -> {
                setState { copy(password = event.value, passwordError = null) }
            }

            // Gender
            is RegisterUiEvent.GenderSelected -> {
                setState { copy(gender = event.gender, genderError = null) }
            }

            // Birth Date
            is RegisterUiEvent.BirthDateSelected -> {
                setState { copy(birthDate = event.date, birthDateError = null) }
            }

            // Password visibility
            RegisterUiEvent.TogglePasswordVisibility -> {
                setState { copy(isPasswordVisible = !isPasswordVisible) }
            }

            // Open date picker
            RegisterUiEvent.BirthDateClick -> {
                sendEffect { RegisterUiEffect.OpenDatePicker }
            }

            // Register
            RegisterUiEvent.RegisterClick -> {
                validateAndRegister()
            }

            // Navigate to login
            RegisterUiEvent.LoginClick -> {
                sendEffect { RegisterUiEffect.NavigateToLogin }
            }
        }
    }

    private fun validateAndRegister() {
        val state = uiState.value
        var hasError = false

        // Email validation
        when {
            state.email.isBlank() -> {
                setState { copy(emailError = "E-posta zorunludur") }
                hasError = true
            }
            !isValidEmail(state.email) -> {
                setState { copy(emailError = "Geçerli bir e-posta girin") }
                hasError = true
            }
        }

        // Password validation
        if (state.password.length < 6) {
            setState { copy(passwordError = "Şifre en az 6 karakter olmalıdır") }
            hasError = true
        }

        // Gender validation
        if (state.gender == null) {
            setState { copy(genderError = "Cinsiyet seçin") }
            hasError = true
        }

        // Birth Date validation
        if (state.birthDate == null) {
            setState { copy(birthDateError = "Doğum tarihi seçin") }
            hasError = true
        }

        if (hasError) return

        // API call
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val birthDateString = state.birthDate!!.toString() // ISO format
            val genderString = state.gender!!.name // "MALE" or "FEMALE"

            authRepository
                    .register(
                            email = state.email,
                            password = state.password,
                            gender = genderString,
                            birthDate = birthDateString
                    )
                    .onSuccess { response ->
                        tokenManager.saveToken(response.token)
                        setState { copy(isLoading = false) }
                        sendEffect { RegisterUiEffect.NavigateToHome }
                    }
                    .onFailure { error ->
                        setState { copy(isLoading = false) }
                        sendEffect {
                            RegisterUiEffect.ShowErrorMessage(
                                    error.message ?: "Kayıt başarısız oldu"
                            )
                        }
                    }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }
}
