package com.ilkinbayramov.ninjatalk.data.repository

import com.ilkinbayramov.ninjatalk.data.ApiClient
import com.ilkinbayramov.ninjatalk.data.dto.Conversation
import com.ilkinbayramov.ninjatalk.data.dto.CreateConversationRequest
import com.ilkinbayramov.ninjatalk.data.dto.Message
import com.ilkinbayramov.ninjatalk.data.dto.SendMessageRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ChatRepository {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun getConversations(token: String): Result<List<Conversation>> {
        return try {
            val response =
                    client.get("$baseUrl/api/chat/conversations") {
                        header("Authorization", "Bearer $token")
                    }

            if (response.status == HttpStatusCode.OK) {
                val conversations: List<Conversation> = response.body()
                Result.success(conversations)
            } else {
                Result.failure(Exception("Failed to get conversations: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessages(conversationId: String, token: String): Result<List<Message>> {
        return try {
            val response =
                    client.get("$baseUrl/api/chat/conversations/$conversationId/messages") {
                        header("Authorization", "Bearer $token")
                    }

            if (response.status == HttpStatusCode.OK) {
                val messages: List<Message> = response.body()
                Result.success(messages)
            } else {
                Result.failure(Exception("Failed to get messages: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(request: SendMessageRequest, token: String): Result<Message> {
        return try {
            val response =
                    client.post("$baseUrl/api/chat/messages") {
                        header("Authorization", "Bearer $token")
                        contentType(ContentType.Application.Json)
                        setBody(request)
                    }

            if (response.status == HttpStatusCode.Created) {
                val message: Message = response.body()
                Result.success(message)
            } else {
                Result.failure(Exception("Failed to send message: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createConversation(otherUserId: String, token: String): Result<String> {
        return try {
            val response =
                    client.post("$baseUrl/api/chat/conversations") {
                        header("Authorization", "Bearer $token")
                        contentType(ContentType.Application.Json)
                        setBody(CreateConversationRequest(otherUserId))
                    }

            if (response.status == HttpStatusCode.OK) {
                val result: Map<String, String> = response.body()
                Result.success(result["conversationId"] ?: "")
            } else {
                Result.failure(Exception("Failed to create conversation: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
