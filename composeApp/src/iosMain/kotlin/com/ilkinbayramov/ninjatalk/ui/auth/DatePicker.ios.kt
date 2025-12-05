package com.ilkinbayramov.ninjatalk.ui.auth

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
actual fun DatePickerDialog(
        currentDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        onDismiss: () -> Unit
) {
    // iOS DatePicker implementation
    // TODO: Implement iOS date picker
}
