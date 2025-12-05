package com.ilkinbayramov.ninjatalk.presentation.auth

import kotlinx.datetime.LocalDate

enum class Gender {
    MALE,
    FEMALE
}

data class RegisterUiState(
        val email: String = "",
        val password: String = "",
        val gender: Gender? = null,
        val birthDate: LocalDate? = null,
        val isPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val genderError: String? = null,
        val birthDateError: String? = null
)

sealed interface RegisterUiEvent {
    data class EmailChanged(val value: String) : RegisterUiEvent
    data class PasswordChanged(val value: String) : RegisterUiEvent
    data object TogglePasswordVisibility : RegisterUiEvent
    data class GenderSelected(val gender: Gender) : RegisterUiEvent
    data class BirthDateSelected(val date: LocalDate) : RegisterUiEvent
    data object BirthDateClick : RegisterUiEvent
    data object RegisterClick : RegisterUiEvent
    data object LoginClick : RegisterUiEvent
}

sealed interface RegisterUiEffect {
    data object OpenDatePicker : RegisterUiEffect
    data object NavigateToLogin : RegisterUiEffect
    data object NavigateToHome : RegisterUiEffect
    data class ShowErrorMessage(val message: String) : RegisterUiEffect
}
