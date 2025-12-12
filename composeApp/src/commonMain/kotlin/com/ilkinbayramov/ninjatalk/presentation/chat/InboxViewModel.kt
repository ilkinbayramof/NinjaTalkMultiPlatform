package com.ilkinbayramov.ninjatalk.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.ninjatalk.data.dto.Message
import com.ilkinbayramov.ninjatalk.data.repository.ChatRepository
import com.ilkinbayramov.ninjatalk.utils.AppConfig
import com.ilkinbayramov.ninjatalk.utils.TokenManager
import com.ilkinbayramov.ninjatalk.websocket.WebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InboxUiState(
        val messages: List<Message> = emptyList(),
        val isLoading: Boolean = false,
        val isSending: Boolean = false,
        val error: String? = null,
        val anonymousName: String = "",
        val isConnected: Boolean = false
)

class InboxViewModel(
        private val conversationId: String,
        private val chatRepository: ChatRepository
) : ViewModel() {

        private val _uiState = MutableStateFlow(InboxUiState())
        val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

        // WebSocket Manager with platform-specific URL
        private val webSocketManager = WebSocketManager(AppConfig.BASE_URL)

        init {
                loadMessages()
                connectWebSocket()
                listenToWebSocket()
        }

        private fun connectWebSocket() {
                viewModelScope.launch {
                        val token = TokenManager.getToken() ?: return@launch
                        println("WS: Connecting with token...")
                        webSocketManager.connect(token)
                }
        }

        private fun listenToWebSocket() {
                viewModelScope.launch {
                        // Listen for connection state
                        webSocketManager.isConnected.collect { connected ->
                                _uiState.value = _uiState.value.copy(isConnected = connected)
                                println("WS: Connection state: $connected")
                        }
                }

                viewModelScope.launch {
                        // Listen for incoming messages
                        webSocketManager.messages.collect { wsMessage ->
                                println("WS: Received message type: ${wsMessage.type}")

                                when (wsMessage.type) {
                                        "new_message" -> {
                                                wsMessage.message?.let { newMessage ->
                                                        if (newMessage.conversationId ==
                                                                        conversationId
                                                        ) {
                                                                println(
                                                                        "WS: Adding new message to UI"
                                                                )
                                                                // Add message if not already in
                                                                // list
                                                                if (_uiState.value.messages.none {
                                                                                it.id ==
                                                                                        newMessage
                                                                                                .id
                                                                        }
                                                                ) {
                                                                        _uiState.value =
                                                                                _uiState.value.copy(
                                                                                        messages =
                                                                                                _uiState.value
                                                                                                        .messages +
                                                                                                        newMessage
                                                                                )
                                                                }
                                                        }
                                                }
                                        }
                                        "typing" -> {
                                                // Handle typing indicator (future feature)
                                                println("WS: User ${wsMessage.userId} is typing")
                                        }
                                        "connected" -> {
                                                println("WS: Connected to server")
                                        }
                                }
                        }
                }
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
                        println("DEBUG: Sending message via WebSocket: $content")
                        _uiState.value = _uiState.value.copy(isSending = true)

                        try {
                                // Send via WebSocket
                                webSocketManager.sendMessage(conversationId, content)
                                _uiState.value = _uiState.value.copy(isSending = false)
                                println("DEBUG: Message sent via WebSocket")
                        } catch (e: Exception) {
                                println("ERROR: Failed to send via WebSocket: ${e.message}")
                                _uiState.value =
                                        _uiState.value.copy(isSending = false, error = e.message)
                        }
                }
        }

        fun setAnonymousName(name: String) {
                _uiState.value = _uiState.value.copy(anonymousName = name)
        }

        override fun onCleared() {
                super.onCleared()
                viewModelScope.launch { webSocketManager.disconnect() }
        }
}
