package com.ilkinbayramov.ninjatalk.presentation.shuffle.filter

data class ShuffleFilterState(
        val minAge: Float = 18f,
        val maxAge: Float = 60f,
        val selectedCountry: String? = null,
        val selectedGender: Gender = Gender.ALL
)

enum class Gender {
    ALL,
    MALE,
    FEMALE,
    OTHER
}

sealed interface ShuffleFilterEvent {
    data class AgeChanged(val min: Float, val max: Float) : ShuffleFilterEvent
    data class CountrySelected(val country: String) : ShuffleFilterEvent
    data class GenderSelected(val gender: Gender) : ShuffleFilterEvent
    data object Reset : ShuffleFilterEvent
    data object Apply : ShuffleFilterEvent
}
