package com.ilkinbayramov.ninjatalk.ui.auth

import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun DatePickerDialog(
        currentDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    androidx.compose.material3.DatePickerDialog(
            onDismissRequest = onDismiss,
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
                            onDismiss()
                        }
                ) { Text("Kaydet") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    ) { DatePicker(state = datePickerState) }
}
