package com.ilkinbayramov.ninjatalk

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ilkinbayramov.ninjatalk.data.repository.AuthRepository
import com.ilkinbayramov.ninjatalk.presentation.auth.RegisterViewModel
import com.ilkinbayramov.ninjatalk.presentation.auth.login.LoginViewModel
import com.ilkinbayramov.ninjatalk.ui.auth.RegisterScreen
import com.ilkinbayramov.ninjatalk.ui.auth.LoginScreen
import com.ilkinbayramov.ninjatalk.ui.main.MainRoute

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf("register") }
        
        val authRepository = remember { AuthRepository() }
        val registerViewModel = remember { RegisterViewModel(authRepository) }
        val loginViewModel = remember { LoginViewModel(authRepository) }
        
        when (currentScreen) {
            "register" -> {
                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateToLogin = { currentScreen = "login" },
                    onNavigateToHome = { currentScreen = "main" }
                )
            }
            "login" -> {
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToRegister = { currentScreen = "register" },
                    onNavigateToHome = { currentScreen = "main" }
                )
            }
            "main" -> {
                MainRoute()
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
