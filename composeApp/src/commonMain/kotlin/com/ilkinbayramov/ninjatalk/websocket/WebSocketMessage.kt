package com.ilkinbayramov.ninjatalk.websocket

import com.ilkinbayramov.ninjatalk.data.dto.Message
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketMessage(
        val type: String, // "send_message", "new_message", "typing", "message_read", "connected"
        val conversationId: String? = null,
        val content: String? = null,
        val message: Message? = null,
        val userId: String? = null
)
