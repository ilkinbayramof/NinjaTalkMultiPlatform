package com.ilkinbayramov.ninjatalk.presentation.shuffle

import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.ninjatalk.core.mvi.MviViewModel
import com.ilkinbayramov.ninjatalk.data.dto.User
import com.ilkinbayramov.ninjatalk.data.repository.UserRepository
import kotlinx.coroutines.launch

data class ShuffleUiState(
        val users: List<User> = emptyList(),
        val filteredUsers: List<User> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentUser: User? = null
)

sealed interface ShuffleUiEvent {
    data class SearchQueryChanged(val query: String) : ShuffleUiEvent
    data class ApplyFilters(
            val filters: com.ilkinbayramov.ninjatalk.presentation.shuffle.filter.ShuffleFilterState
    ) : ShuffleUiEvent
    data object Refresh : ShuffleUiEvent
}

sealed interface ShuffleUiEffect {
    data class ShowError(val message: String) : ShuffleUiEffect
    data object NavigateToPremium : ShuffleUiEffect
}

class ShuffleViewModel(
        private val userRepository: UserRepository,
        initialUser: com.ilkinbayramov.ninjatalk.data.dto.User?
) :
        MviViewModel<ShuffleUiEvent, ShuffleUiState, ShuffleUiEffect>(
                initialState = ShuffleUiState(currentUser = initialUser)
        ) {

    init {
        loadUsers()
    }

    override fun onEvent(event: ShuffleUiEvent) {
        when (event) {
            is ShuffleUiEvent.SearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                filterUsers()
            }
            is ShuffleUiEvent.ApplyFilters -> applyFilters(event.filters)
            ShuffleUiEvent.Refresh -> loadUsers()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            userRepository
                    .getAllUsers()
                    .onSuccess { users ->
                        setState { copy(users = users, isLoading = false) }
                        filterUsers()
                    }
                    .onFailure { error ->
                        setState { copy(isLoading = false, error = error.message) }
                        sendEffect {
                            ShuffleUiEffect.ShowError(error.message ?: "Kullanıcılar yüklenemedi")
                        }
                    }
        }
    }

    private fun filterUsers() {
        val query = uiState.value.searchQuery
        val allUsers = uiState.value.users

        val filtered =
                if (query.isBlank()) {
                    allUsers
                } else {
                    allUsers.filter { user ->
                        // Filter by email (or name if we had it, currently only email in User DTO?)
                        // Wait, User DTO has email, gender, birthDate, bio. No separate name field
                        // yet.
                        // We should probably derive a display name from email or add a name field
                        // later.
                        // For now, let's filter by email.
                        user.email.contains(query, ignoreCase = true) ||
                                (user.bio?.contains(query, ignoreCase = true) == true)
                    }
                }

        setState { copy(filteredUsers = filtered) }
    }

    private fun applyFilters(
            filters: com.ilkinbayramov.ninjatalk.presentation.shuffle.filter.ShuffleFilterState
    ) {
        viewModelScope.launch {
            // Check if user is premium (currentUser comes from MainRoute)
            val currentUser = uiState.value.currentUser
            println(
                    "🔍 SHUFFLE: Applying filters - email=${currentUser?.email}, isPremium=${currentUser?.isPremium}"
            )

            if (currentUser?.isPremium != true) {
                // Not premium - navigate to premium tab
                println("⚠️ SHUFFLE: User is NOT premium - navigating to Premium tab")
                sendEffect { ShuffleUiEffect.NavigateToPremium }
                return@launch
            }

            println("✅ SHUFFLE: User IS premium - applying filters!")

            // Premium user - apply filters
            setState { copy(isLoading = true, error = null) }

            val minAge = filters.minAge.toInt()
            val maxAge = filters.maxAge.toInt()
            val gender =
                    when (filters.selectedGender) {
                        com.ilkinbayramov.ninjatalk.presentation.shuffle.filter.Gender.MALE ->
                                "male"
                        com.ilkinbayramov.ninjatalk.presentation.shuffle.filter.Gender.FEMALE ->
                                "female"
                        com.ilkinbayramov.ninjatalk.presentation.shuffle.filter.Gender.ALL -> null
                        else -> null
                    }

            userRepository
                    .getAllUsers(minAge, maxAge, gender)
                    .onSuccess { users ->
                        setState { copy(users = users, isLoading = false) }
                        filterUsers()
                    }
                    .onFailure { error ->
                        setState { copy(isLoading = false, error = error.message) }
                        sendEffect {
                            ShuffleUiEffect.ShowError(error.message ?: "Filtre uygulanamadı")
                        }
                    }
        }
    }
}
