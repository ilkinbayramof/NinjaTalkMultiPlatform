package com.ilkinbayramov.ninjatalk.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.ninjatalk.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val currentPasswordError: String? = null,
        val newPasswordError: String? = null,
        val confirmPasswordError: String? = null,
        val isCurrentPasswordVisible: Boolean = false,
        val isNewPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val successMessage: String? = null,
        val errorMessage: String? = null
)

class ChangePasswordViewModel(private val userRepository: UserRepository) : ViewModel() {

        private val _uiState = MutableStateFlow(ChangePasswordUiState())
        val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

        fun onCurrentPasswordChange(password: String) {
                _uiState.value =
                        _uiState.value.copy(
                                currentPassword = password,
                                currentPasswordError = null,
                                errorMessage = null
                        )
        }

        fun onNewPasswordChange(password: String) {
                _uiState.value =
                        _uiState.value.copy(
                                newPassword = password,
                                newPasswordError = null,
                                errorMessage = null
                        )
        }

        fun onConfirmPasswordChange(password: String) {
                _uiState.value =
                        _uiState.value.copy(
                                confirmPassword = password,
                                confirmPasswordError = null,
                                errorMessage = null
                        )
        }

        fun toggleCurrentPasswordVisibility() {
                _uiState.value =
                        _uiState.value.copy(
                                isCurrentPasswordVisible = !_uiState.value.isCurrentPasswordVisible
                        )
        }

        fun toggleNewPasswordVisibility() {
                _uiState.value =
                        _uiState.value.copy(
                                isNewPasswordVisible = !_uiState.value.isNewPasswordVisible
                        )
        }

        fun toggleConfirmPasswordVisibility() {
                _uiState.value =
                        _uiState.value.copy(
                                isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
                        )
        }

        fun changePassword() {
                val state = _uiState.value
                var hasError = false

                // Validate current password
                if (state.currentPassword.isBlank()) {
                        _uiState.value =
                                _uiState.value.copy(
                                        currentPasswordError = "Mevcut şifre zorunludur"
                                )
                        hasError = true
                }

                // Validate new password
                if (state.newPassword.length < 6) {
                        _uiState.value =
                                _uiState.value.copy(
                                        newPasswordError = "Yeni şifre en az 6 karakter olmalıdır"
                                )
                        hasError = true
                }

                // Validate confirm password
                if (state.confirmPassword != state.newPassword) {
                        _uiState.value =
                                _uiState.value.copy(confirmPasswordError = "Şifreler eşleşmiyor")
                        hasError = true
                }

                if (hasError) return

                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true)

                        userRepository
                                .changePassword(
                                        state.currentPassword.trim(),
                                        state.newPassword.trim()
                                )
                                .onSuccess {
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        successMessage =
                                                                "Şifre başarıyla değiştirildi",
                                                        currentPassword = "",
                                                        newPassword = "",
                                                        confirmPassword = ""
                                                )
                                }
                                .onFailure { error ->
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        errorMessage = error.message
                                                                        ?: "Şifre değiştirme başarısız oldu"
                                                )
                                }
                }
        }

        fun clearMessages() {
                _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
        }
}
