package com.ilkinbayramov.ninjatalk.presentation.shuffle.filter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShuffleFilterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ShuffleFilterState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ShuffleFilterEvent) {
        when (event) {
            is ShuffleFilterEvent.AgeChanged ->
                    _uiState.value = _uiState.value.copy(minAge = event.min, maxAge = event.max)
            is ShuffleFilterEvent.CountrySelected ->
                    _uiState.value = _uiState.value.copy(selectedCountry = event.country)
            is ShuffleFilterEvent.GenderSelected ->
                    _uiState.value = _uiState.value.copy(selectedGender = event.gender)
            ShuffleFilterEvent.Reset -> _uiState.value = ShuffleFilterState()
            ShuffleFilterEvent.Apply -> {
                // Filter results sent to ShuffleViewModel
            }
        }
    }
}
