package com.ilkinbayramov.ninjatalk.ui.settings.password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ilkinbayramov.ninjatalk.data.repository.UserRepository
import com.ilkinbayramov.ninjatalk.presentation.settings.ChangePasswordViewModel
import com.ilkinbayramov.ninjatalk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBackClick: () -> Unit) {
    val viewModel = remember { ChangePasswordViewModel(UserRepository()) }
    val state by viewModel.uiState.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    // Show success dialog
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    viewModel.clearMessages()
                    onBackClick()
                },
                title = { Text("Başarılı") },
                text = { Text(state.successMessage ?: "") },
                confirmButton = {
                    TextButton(
                            onClick = {
                                showSuccessDialog = false
                                viewModel.clearMessages()
                                onBackClick()
                            }
                    ) { Text("Tamam") }
                },
                containerColor = NinjaSurface
        )
    }

    Scaffold(
            containerColor = NinjaBackground,
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = "Şifre Değiştir",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                            }
                        },
                        actions = {
                            TextButton(
                                    onClick = { viewModel.changePassword() },
                                    enabled = !state.isLoading
                            ) {
                                Text(
                                        text = "Kaydet",
                                        color =
                                                if (state.isLoading) NinjaTextSecondary
                                                else NinjaPrimary,
                                        fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = NinjaSurface)
                )
            }
    ) { padding ->
        Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message
            if (state.errorMessage != null) {
                Surface(color = Color.Red.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                            text = state.errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Current Password
            OutlinedTextField(
                    value = state.currentPassword,
                    onValueChange = { viewModel.onCurrentPasswordChange(it) },
                    label = { Text("Mevcut Şifre") },
                    visualTransformation =
                            if (state.isCurrentPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleCurrentPasswordVisibility() }) {
                            Icon(
                                    imageVector =
                                            if (state.isCurrentPasswordVisible)
                                                    Icons.Default.Visibility
                                            else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = NinjaTextSecondary
                            )
                        }
                    },
                    isError = state.currentPasswordError != null,
                    supportingText = state.currentPasswordError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NinjaSurface,
                                    unfocusedContainerColor = NinjaSurface,
                                    cursorColor = NinjaPrimary,
                                    focusedBorderColor = NinjaPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = NinjaPrimary,
                                    unfocusedLabelColor = NinjaTextSecondary
                            )
            )

            // New Password
            OutlinedTextField(
                    value = state.newPassword,
                    onValueChange = { viewModel.onNewPasswordChange(it) },
                    label = { Text("Yeni Şifre") },
                    visualTransformation =
                            if (state.isNewPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleNewPasswordVisibility() }) {
                            Icon(
                                    imageVector =
                                            if (state.isNewPasswordVisible) Icons.Default.Visibility
                                            else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = NinjaTextSecondary
                            )
                        }
                    },
                    isError = state.newPasswordError != null,
                    supportingText = state.newPasswordError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NinjaSurface,
                                    unfocusedContainerColor = NinjaSurface,
                                    cursorColor = NinjaPrimary,
                                    focusedBorderColor = NinjaPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = NinjaPrimary,
                                    unfocusedLabelColor = NinjaTextSecondary
                            )
            )

            // Confirm Password
            OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = { Text("Yeni Şifre (Tekrar)") },
                    visualTransformation =
                            if (state.isConfirmPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                            Icon(
                                    imageVector =
                                            if (state.isConfirmPasswordVisible)
                                                    Icons.Default.Visibility
                                            else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = NinjaTextSecondary
                            )
                        }
                    },
                    isError = state.confirmPasswordError != null,
                    supportingText = state.confirmPasswordError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = NinjaSurface,
                                    unfocusedContainerColor = NinjaSurface,
                                    cursorColor = NinjaPrimary,
                                    focusedBorderColor = NinjaPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = NinjaPrimary,
                                    unfocusedLabelColor = NinjaTextSecondary
                            )
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                        color = NinjaPrimary,
                        modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
