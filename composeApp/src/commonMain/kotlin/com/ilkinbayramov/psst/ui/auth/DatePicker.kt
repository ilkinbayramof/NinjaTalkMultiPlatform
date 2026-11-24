package com.ilkinbayramov.psst.ui.auth

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
expect fun DatePickerDialog(
        currentDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        onDismiss: () -> Unit
)
