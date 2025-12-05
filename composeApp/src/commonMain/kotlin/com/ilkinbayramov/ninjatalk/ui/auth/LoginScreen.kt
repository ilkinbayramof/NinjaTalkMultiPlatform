package com.ilkinbayramov.ninjatalk.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.presentation.auth.login.*
import com.ilkinbayramov.ninjatalk.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
        viewModel: LoginViewModel,
        onNavigateToRegister: () -> Unit,
        onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginUiEffect.ShowErrorMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                LoginUiEffect.NavigateToHome -> onNavigateToHome()
                LoginUiEffect.NavigateToRegister -> onNavigateToRegister()
            }
        }
    }

    Scaffold(
            containerColor = NinjaBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LoginScreenContent(
                state = state,
                onEmailChanged = { viewModel.onEvent(LoginUiEvent.EmailChanged(it)) },
                onPasswordChanged = { viewModel.onEvent(LoginUiEvent.PasswordChanged(it)) },
                onTogglePasswordVisibility = {
                    viewModel.onEvent(LoginUiEvent.TogglePasswordVisibility)
                },
                onLoginClick = { viewModel.onEvent(LoginUiEvent.LoginClick) },
                onNavigateToRegister = { viewModel.onEvent(LoginUiEvent.RegisterClick) },
                modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun LoginScreenContent(
        state: LoginUiState,
        onEmailChanged: (String) -> Unit,
        onPasswordChanged: (String) -> Unit,
        onTogglePasswordVisibility: () -> Unit,
        onLoginClick: () -> Unit,
        onNavigateToRegister: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier =
                    modifier.fillMaxSize().background(NinjaBackground).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Box(
                modifier = Modifier.size(90.dp).background(NinjaSurface, CircleShape),
                contentAlignment = Alignment.Center
        ) { Text("💬", fontSize = 34.sp) }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
                text = "Gerçek Seni Keşfet",
                style =
                        MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                        )
        )

        Spacer(modifier = Modifier.height(28.dp))

        NinjaTextField(
                label = "Kullanıcı Adı veya E-posta",
                placeholder = "Kullanıcı adınızı veya e-postanızı girin",
                value = state.email,
                onValueChange = onEmailChanged,
                errorText = state.emailError,
                keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        NinjaTextField(
                label = "Şifre",
                placeholder = "Şifrenizi girin",
                value = state.password,
                onValueChange = onPasswordChanged,
                errorText = state.passwordError,
                isPassword = true,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                    text = "Şifremi Unuttum?",
                    color = NinjaPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier =
                            Modifier.clickable {
                                // TODO: Şifre sıfırlama
                            }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
                onClick = onLoginClick,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(50),
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = NinjaPrimary,
                                disabledContainerColor = NinjaPrimary.copy(alpha = 0.4f)
                        )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                )
            } else {
                Text(text = "Giriş Yap", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text(text = "Hesabın yok mu? ", color = NinjaTextSecondary)
            Text(
                    text = "Kaydol",
                    color = NinjaPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
                text = "Gizlilik Politikası  ·  Kullanım Koşulları",
                color = NinjaTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
        )
    }
}
