package com.ilkinbayramov.ninjatalk.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.ninjatalk.data.dto.Message
import com.ilkinbayramov.ninjatalk.data.dto.SendMessageRequest
import com.ilkinbayramov.ninjatalk.data.repository.ChatRepository
import com.ilkinbayramov.ninjatalk.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InboxUiState(
        val messages: List<Message> = emptyList(),
        val isLoading: Boolean = false,
        val isSending: Boolean = false,
        val error: String? = null,
        val anonymousName: String = ""
)

class InboxViewModel(
        private val conversationId: String,
        private val chatRepository: ChatRepository
) : ViewModel() {

        private val _uiState = MutableStateFlow(InboxUiState())
        val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

        private var pollingJob: kotlinx.coroutines.Job? = null

        init {
                loadMessages()
                startPolling()
        }

        private fun startPolling() {
                pollingJob =
                        viewModelScope.launch {
                                while (true) {
                                        kotlinx.coroutines.delay(3000) // Check every 3 seconds
                                        loadMessages(showLoading = false) // Silent refresh
                                }
                        }
        }

        override fun onCleared() {
                super.onCleared()
                pollingJob?.cancel()
        }

        fun loadMessages(showLoading: Boolean = true) {
                viewModelScope.launch {
                        if (showLoading) {
                                _uiState.value = _uiState.value.copy(isLoading = true)
                        }

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
                                .getMessages(conversationId, token)
                                .onSuccess { messages ->
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        messages = messages,
                                                        isLoading = false,
                                                        error = null
                                                )
                                }
                                .onFailure { error ->
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        error = error.message
                                                )
                                }
                }
        }

        fun sendMessage(content: String) {
                if (content.isBlank()) return

                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isSending = true)

                        val token =
                                TokenManager.getToken()
                                        ?: run {
                                                _uiState.value =
                                                        _uiState.value.copy(
                                                                isSending = false,
                                                                error = "Not authenticated"
                                                        )
                                                return@launch
                                        }

                        val request = SendMessageRequest(conversationId, content)
                        chatRepository
                                .sendMessage(request, token)
                                .onSuccess { newMessage ->
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        messages =
                                                                _uiState.value.messages +
                                                                        newMessage,
                                                        isSending = false,
                                                        error = null
                                                )
                                }
                                .onFailure { error ->
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isSending = false,
                                                        error = error.message
                                                )
                                }
                }
        }

        fun setAnonymousName(name: String) {
                _uiState.value = _uiState.value.copy(anonymousName = name)
        }
}
