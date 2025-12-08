package com.ilkinbayramov.ninjatalk.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.ninjatalk.data.dto.Conversation
import com.ilkinbayramov.ninjatalk.data.repository.ChatRepository
import com.ilkinbayramov.ninjatalk.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatListUiState(
        val conversations: List<Conversation> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
)

class ChatListViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val token =
                    TokenManager.getToken()
                            ?: run {
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                error = "Not authenticated"
                                        )
                                return@launch
                            }

            chatRepository
                    .getConversations(token)
                    .onSuccess { conversations ->
                        _uiState.value =
                                _uiState.value.copy(
                                        conversations = conversations,
                                        isLoading = false,
                                        error = null
                                )
                    }
                    .onFailure { error ->
                        _uiState.value =
                                _uiState.value.copy(isLoading = false, error = error.message)
                    }
        }
    }
}
