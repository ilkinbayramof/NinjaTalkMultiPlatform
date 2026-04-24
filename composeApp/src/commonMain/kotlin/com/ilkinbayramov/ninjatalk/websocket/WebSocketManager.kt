package com.ilkinbayramov.ninjatalk.websocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WebSocketManager(private val baseUrl: String) {
    private val client = createHttpClient()

    private var session: DefaultClientWebSocketSession? = null

    // replay=1 + extraBufferCapacity=10: collector hazır olmasa belə mesaj itmir
    private val _messages = MutableSharedFlow<WebSocketMessage>(
        replay = 1,
        extraBufferCapacity = 10
    )
    val messages: SharedFlow<WebSocketMessage> = _messages.asSharedFlow()

    private val _connectionState = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _connectionState.asStateFlow()

    suspend fun connect(token: String) {
        if (_connectionState.value) {
            println("WS: Already connected")
            return
        }

        try {
            val wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://")
            println("WS: Connecting to $wsUrl/ws/chat")

            session = client.webSocketSession {
                url("$wsUrl/ws/chat")
                header("Authorization", "Bearer $token")
            }

            _connectionState.value = true
            println("WS: Connected successfully")

            // Listen for incoming messages
            session?.incoming?.consumeAsFlow()?.collect { frame ->
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    println("WS: Received: $text")
                    try {
                        val message = Json.decodeFromString<WebSocketMessage>(text)
                        _messages.emit(message)
                    } catch (e: Exception) {
                        println("WS: Parse error: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("WS: Connection error: ${e.message}")
        } finally {
            // HƏM normal disconnect, HƏM exception-da mütləq false olmalıdır!
            // Əvvəlki kodda bu yox idi — reconnect mümkün olmurdu
            _connectionState.value = false
            session = null
            println("WS: Disconnected")
        }
    }

    suspend fun sendMessage(conversationId: String, content: String) {
        val message = WebSocketMessage(
            type = "send_message",
            conversationId = conversationId,
            content = content
        )
        val json = Json.encodeToString(message)
        println("WS: Sending: $json")
        try {
            session?.send(Frame.Text(json))
        } catch (e: Exception) {
            println("WS: Send error: ${e.message}")
        }
    }

    suspend fun sendTyping(conversationId: String) {
        val message = WebSocketMessage(type = "typing", conversationId = conversationId)
        try {
            session?.send(Frame.Text(Json.encodeToString(message)))
        } catch (e: Exception) {
            println("WS: Typing send error: ${e.message}")
        }
    }

    suspend fun disconnect() {
        try {
            session?.close()
        } catch (e: Exception) {
            println("WS: Disconnect error: ${e.message}")
        } finally {
            session = null
            _connectionState.value = false
            println("WS: Disconnected")
        }
    }
}
