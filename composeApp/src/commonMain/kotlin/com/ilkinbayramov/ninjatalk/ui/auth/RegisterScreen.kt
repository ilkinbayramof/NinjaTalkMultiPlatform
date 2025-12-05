package com.ilkinbayramov.ninjatalk.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilkinbayramov.ninjatalk.presentation.auth.*
import com.ilkinbayramov.ninjatalk.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
        viewModel: RegisterViewModel,
        onNavigateToLogin: () -> Unit,
        onNavigateToHome: () -> Unit
) {
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        var showDatePicker by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
                viewModel.effect.collectLatest { effect ->
                        when (effect) {
                                is RegisterUiEffect.ShowErrorMessage -> {
                                        snackbarHostState.showSnackbar(effect.message)
                                }
                                RegisterUiEffect.NavigateToHome -> onNavigateToHome()
                                RegisterUiEffect.NavigateToLogin -> onNavigateToLogin()
                                RegisterUiEffect.OpenDatePicker -> {
                                        showDatePicker = true
                                }
                        }
                }
        }

        if (showDatePicker) {
                DatePickerDialog(
                        currentDate = state.birthDate,
                        onDateSelected = { date ->
                                viewModel.onEvent(RegisterUiEvent.BirthDateSelected(date))
                                showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                )
        }

        Scaffold(
                containerColor = NinjaBackground,
                snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
                RegisterScreenContent(
                        state = state,
                        onEmailChanged = { viewModel.onEvent(RegisterUiEvent.EmailChanged(it)) },
                        onPasswordChanged = {
                                viewModel.onEvent(RegisterUiEvent.PasswordChanged(it))
                        },
                        onTogglePasswordVisibility = {
                                viewModel.onEvent(RegisterUiEvent.TogglePasswordVisibility)
                        },
                        onGenderSelected = {
                                viewModel.onEvent(RegisterUiEvent.GenderSelected(it))
                        },
                        onBirthDateClick = { viewModel.onEvent(RegisterUiEvent.BirthDateClick) },
                        onRegisterClick = { viewModel.onEvent(RegisterUiEvent.RegisterClick) },
                        onLoginClick = { viewModel.onEvent(RegisterUiEvent.LoginClick) },
                        modifier = Modifier.padding(padding)
                )
        }
}

@Composable
private fun RegisterScreenContent(
        state: RegisterUiState,
        onEmailChanged: (String) -> Unit,
        onPasswordChanged: (String) -> Unit,
        onTogglePasswordVisibility: () -> Unit,
        onGenderSelected: (Gender) -> Unit,
        onBirthDateClick: () -> Unit,
        onRegisterClick: () -> Unit,
        onLoginClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Column(
                modifier =
                        modifier.fillMaxSize()
                                .background(NinjaBackground)
                                .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                        modifier =
                                Modifier.size(80.dp)
                                        .background(color = NinjaSurface, shape = CircleShape),
                        contentAlignment = Alignment.Center
                ) { Text(text = "💬", fontSize = 32.sp) }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                        text = "Hesabını Oluştur",
                        style =
                                MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                )

                Spacer(modifier = Modifier.height(24.dp))

                NinjaTextField(
                        label = "E-posta Adresi",
                        placeholder = "E-posta adresini gir",
                        value = state.email,
                        onValueChange = onEmailChanged,
                        errorText = state.emailError,
                        keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                NinjaTextField(
                        label = "Şifre",
                        placeholder = "Bir şifre belirle",
                        value = state.password,
                        onValueChange = onPasswordChanged,
                        errorText = state.passwordError,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        isPasswordVisible = state.isPasswordVisible,
                        onTogglePasswordVisibility = onTogglePasswordVisibility
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                                text = "Cinsiyet",
                                style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                                color = NinjaTextSecondary
                                        )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                GenderChip(
                                        text = "Erkek",
                                        isSelected = state.gender == Gender.MALE,
                                        onClick = { onGenderSelected(Gender.MALE) }
                                )
                                GenderChip(
                                        text = "Kadın",
                                        isSelected = state.gender == Gender.FEMALE,
                                        onClick = { onGenderSelected(Gender.FEMALE) }
                                )
                        }
                        if (state.genderError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = state.genderError,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                                text = "Doğum Tarihi",
                                style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                                color = NinjaTextSecondary
                                        )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(56.dp)
                                                .background(
                                                        color = NinjaSurface,
                                                        shape = RoundedCornerShape(50)
                                                )
                                                .clickable { onBirthDateClick() }
                                                .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterStart
                        ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        val dateText =
                                                state.birthDate?.let {
                                                        "${it.dayOfMonth.toString().padStart(2, '0')}/${it.monthNumber.toString().padStart(2, '0')}/${it.year}"
                                                }
                                                        ?: "GG/AA/YYYY"

                                        Text(
                                                text = dateText,
                                                color =
                                                        if (state.birthDate == null)
                                                                NinjaTextSecondary
                                                        else Color.White
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                                imageVector = Icons.Outlined.CalendarToday,
                                                contentDescription = null,
                                                tint = NinjaTextSecondary
                                        )
                                }
                        }
                        if (state.birthDateError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = state.birthDateError,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                        onClick = onRegisterClick,
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
                                Text(
                                        text = "Kaydol",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onLoginClick) {
                        Text(text = "Zaten bir hesabın var mı? ", color = NinjaTextSecondary)
                        Text(
                                text = "Giriş Yap",
                                color = NinjaPrimary,
                                fontWeight = FontWeight.SemiBold
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        text = "Gizlilik Politikası · Kullanım Koşulları",
                        style =
                                MaterialTheme.typography.bodySmall.copy(
                                        color = NinjaTextSecondary,
                                        textAlign = TextAlign.Center
                                ),
                        modifier = Modifier.fillMaxWidth()
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NinjaTextField(
        label: String,
        placeholder: String,
        value: String,
        onValueChange: (String) -> Unit,
        errorText: String?,
        keyboardType: KeyboardType,
        isPassword: Boolean = false,
        isPasswordVisible: Boolean = false,
        onTogglePasswordVisibility: (() -> Unit)? = null
) {
        Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(color = NinjaTextSecondary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = placeholder, color = NinjaTextSecondary) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        visualTransformation =
                                if (isPassword && !isPasswordVisible) PasswordVisualTransformation()
                                else VisualTransformation.None,
                        trailingIcon = {
                                if (isPassword && onTogglePasswordVisibility != null) {
                                        IconButton(onClick = onTogglePasswordVisibility) {
                                                Icon(
                                                        imageVector =
                                                                if (isPasswordVisible)
                                                                        Icons.Default.VisibilityOff
                                                                else Icons.Default.Visibility,
                                                        contentDescription = null,
                                                        tint = NinjaTextSecondary
                                                )
                                        }
                                }
                        },
                        colors =
                                TextFieldDefaults.colors(
                                        focusedContainerColor = NinjaSurface,
                                        unfocusedContainerColor = NinjaSurface,
                                        disabledContainerColor = NinjaSurface,
                                        errorContainerColor = NinjaSurface,
                                        focusedIndicatorColor = NinjaPrimary,
                                        unfocusedIndicatorColor = NinjaPrimary.copy(alpha = 0.4f),
                                        cursorColor = NinjaPrimary,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                )
                )

                if (errorText != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                text = errorText,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                        )
                }
        }
}

@Composable
private fun GenderChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
        Surface(
                modifier = Modifier.height(40.dp).clickable { onClick() },
                shape = RoundedCornerShape(50),
                color = if (isSelected) NinjaPrimary.copy(alpha = 0.2f) else NinjaSurface,
                border =
                        if (isSelected) {
                                BorderStroke(1.dp, NinjaPrimary)
                        } else null
        ) {
                Box(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                ) {
                        Text(
                                text = text,
                                color = if (isSelected) Color.White else NinjaTextSecondary,
                                fontWeight =
                                        if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                }
        }
}
