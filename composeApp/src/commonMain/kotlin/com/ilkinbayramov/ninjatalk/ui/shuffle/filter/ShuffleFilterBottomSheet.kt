package com.ilkinbayramov.ninjatalk.ui.shuffle.filter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ilkinbayramov.ninjatalk.presentation.shuffle.filter.*
import com.ilkinbayramov.ninjatalk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShuffleFilterBottomSheet(
        viewModel: ShuffleFilterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
        onDismiss: () -> Unit,
        onApplyFilters: (ShuffleFilterState) -> Unit
) {
        val state by viewModel.uiState.collectAsState()

        ModalBottomSheet(
                onDismissRequest = onDismiss,
                containerColor = NinjaBackground,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        // HEADER
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Text(
                                        "Sıfırla",
                                        color = NinjaPrimary,
                                        modifier =
                                                Modifier.weight(1f).clickable {
                                                        viewModel.onEvent(ShuffleFilterEvent.Reset)
                                                }
                                )
                                Text(
                                        "Filtrele",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                        "Kapat",
                                        color = NinjaPrimary,
                                        modifier = Modifier.weight(1f).clickable { onDismiss() },
                                        textAlign = TextAlign.End
                                )
                        }

                        Spacer(Modifier.height(24.dp))

                        // AGE
                        Text("YAŞ", color = NinjaTextSecondary)
                        RangeSlider(
                                value = state.minAge..state.maxAge,
                                onValueChange = {
                                        viewModel.onEvent(
                                                ShuffleFilterEvent.AgeChanged(
                                                        it.start,
                                                        it.endInclusive
                                                )
                                        )
                                },
                                valueRange = 18f..60f,
                                colors =
                                        SliderDefaults.colors(
                                                activeTrackColor = NinjaPrimary,
                                                thumbColor = NinjaPrimary
                                        )
                        )
                        Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Text("${state.minAge.toInt()}", color = Color.White)
                                Text("${state.maxAge.toInt()}", color = Color.White)
                        }

                        Spacer(Modifier.height(24.dp))

                        // COUNTRY
                        Text("ÜLKE", color = NinjaTextSecondary)
                        FilterRowItem(
                                text = state.selectedCountry ?: "Seç",
                                onClick = {
                                        // TODO: Country picker
                                }
                        )

                        Spacer(Modifier.height(24.dp))

                        // GENDER
                        Text("CİNSİYET", color = NinjaTextSecondary)
                        GenderChips(
                                selected = state.selectedGender,
                                onSelected = {
                                        viewModel.onEvent(ShuffleFilterEvent.GenderSelected(it))
                                }
                        )

                        Spacer(Modifier.height(36.dp))

                        // APPLY BUTTON
                        Button(
                                onClick = {
                                        onApplyFilters(state)
                                        onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NinjaPrimary)
                        ) { Text("Filtreleri Uygula", fontWeight = FontWeight.SemiBold) }

                        Spacer(Modifier.height(24.dp))
                }
        }
}

@Composable
private fun FilterRowItem(text: String, onClick: () -> Unit) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(48.dp)
                                .background(NinjaSurface, RoundedCornerShape(12.dp))
                                .clickable(onClick = onClick)
                                .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(text, color = Color.White, modifier = Modifier.weight(1f))
                Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = NinjaTextSecondary
                )
        }
}

@Composable
private fun GenderChips(selected: Gender, onSelected: (Gender) -> Unit) {
        val options =
                listOf(
                        "Tümü" to Gender.ALL,
                        "Erkek" to Gender.MALE,
                        "Kadın" to Gender.FEMALE,
                        "Diğer" to Gender.OTHER
                )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { (label, gender) ->
                        Surface(
                                shape = RoundedCornerShape(50),
                                color =
                                        if (selected == gender) NinjaPrimary.copy(alpha = 0.2f)
                                        else NinjaSurface,
                                border =
                                        if (selected == gender) BorderStroke(1.dp, NinjaPrimary)
                                        else null,
                                modifier = Modifier.clickable { onSelected(gender) }
                        ) {
                                Box(
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 20.dp,
                                                        vertical = 10.dp
                                                ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                label,
                                                color =
                                                        if (selected == gender) Color.White
                                                        else NinjaTextSecondary
                                        )
                                }
                        }
                }
        }
}
