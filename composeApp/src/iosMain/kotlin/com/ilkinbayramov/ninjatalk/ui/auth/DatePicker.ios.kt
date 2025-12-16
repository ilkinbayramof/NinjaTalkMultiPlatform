package com.ilkinbayramov.ninjatalk.ui.auth

import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class, kotlin.time.ExperimentalTime::class)
@Composable
actual fun DatePickerDialog(
        currentDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    // Use AlertDialog for iOS
    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Doğum Tarihi Seç") },
            text = { DatePicker(state = datePickerState, showModeToggle = false) },
            confirmButton = {
                TextButton(
                        onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val date =
                                        Instant.fromEpochMilliseconds(millis)
                                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                                .date
                                onDateSelected(date)
                            }
                        }
                ) { Text("Kaydet") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
