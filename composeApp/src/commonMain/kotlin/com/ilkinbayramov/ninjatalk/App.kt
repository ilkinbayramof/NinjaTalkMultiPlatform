package com.ilkinbayramov.ninjatalk

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ilkinbayramov.ninjatalk.data.repository.AuthRepository
import com.ilkinbayramov.ninjatalk.presentation.auth.RegisterViewModel
import com.ilkinbayramov.ninjatalk.presentation.auth.login.LoginViewModel
import com.ilkinbayramov.ninjatalk.ui.auth.LoginScreen
import com.ilkinbayramov.ninjatalk.ui.auth.RegisterScreen
import com.ilkinbayramov.ninjatalk.ui.main.MainRoute
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf("register") }
        val scope = rememberCoroutineScope()

        val authRepository = remember { AuthRepository() }

        when (currentScreen) {
            "register" -> {
                val registerViewModel =
                        remember(currentScreen) { RegisterViewModel(authRepository) }
                RegisterScreen(
                        viewModel = registerViewModel,
                        onNavigateToLogin = { currentScreen = "login" },
                        onNavigateToHome = { currentScreen = "main" }
                )
            }
            "login" -> {
                val loginViewModel = remember(currentScreen) { LoginViewModel(authRepository) }
                LoginScreen(
                        viewModel = loginViewModel,
                        onNavigateToRegister = { currentScreen = "register" },
                        onNavigateToHome = { currentScreen = "main" }
                )
            }
            "main" -> {
                MainRoute(
                        onLogout = {
                            // Clear token and navigate to login
                            scope.launch {
                                com.ilkinbayramov.ninjatalk.utils.TokenManager.clearToken()
                            }
                            currentScreen = "login"
                        }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}
