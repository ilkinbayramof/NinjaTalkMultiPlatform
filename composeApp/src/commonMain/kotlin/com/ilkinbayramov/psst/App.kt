package com.ilkinbayramov.psst

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ilkinbayramov.psst.data.repository.AuthRepository
import com.ilkinbayramov.psst.presentation.auth.RegisterViewModel
import com.ilkinbayramov.psst.presentation.auth.login.LoginViewModel
import com.ilkinbayramov.psst.ui.auth.LoginScreen
import com.ilkinbayramov.psst.ui.auth.RegisterScreen
import com.ilkinbayramov.psst.ui.main.MainRoute
import com.ilkinbayramov.psst.localization.*
import kotlinx.coroutines.launch

val LocalLanguageController = staticCompositionLocalOf<(AppLanguage) -> Unit> { {} }
val LocalAppLanguage = compositionLocalOf { AppLanguage.TURKISH }

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<String?>(null) } // null = checking
    var currentLanguage by remember { mutableStateOf(AppLanguage.TURKISH) }
    var isLanguageLoaded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

        val authRepository = remember { AuthRepository() }

        // Check for existing token and language on app start
        LaunchedEffect(Unit) {
            val savedLangCode = com.ilkinbayramov.psst.utils.TokenManager.getLanguage()
            currentLanguage = AppLanguage.fromCode(savedLangCode)
            isLanguageLoaded = true
            
            val token = com.ilkinbayramov.psst.utils.TokenManager.getToken()
            currentScreen = if (token != null) "main" else "register"
        }

        // Show loading while checking token or language
        if (currentScreen == null || !isLanguageLoaded) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...", style = MaterialTheme.typography.bodyLarge)
            }
            return
        }

        val appStrings = if (currentLanguage == AppLanguage.AZERBAIJANI) azStrings else trStrings
        val changeLanguage: (AppLanguage) -> Unit = { newLang ->
            currentLanguage = newLang
            scope.launch {
                com.ilkinbayramov.psst.utils.TokenManager.setLanguage(newLang.code)
            }
        }

        CompositionLocalProvider(
            LocalAppStrings provides appStrings,
            LocalLanguageController provides changeLanguage,
            LocalAppLanguage provides currentLanguage
        ) {
            MaterialTheme {
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
                                com.ilkinbayramov.psst.utils.TokenManager.clearToken()
                            }
                            currentScreen = "login"
                        }
                )
            }
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
