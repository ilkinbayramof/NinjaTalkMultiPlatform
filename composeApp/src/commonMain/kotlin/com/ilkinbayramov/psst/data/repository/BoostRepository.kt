package com.ilkinbayramov.psst.data.repository

import com.ilkinbayramov.psst.data.ApiClient
import com.ilkinbayramov.psst.data.dto.BoostStatus
import com.ilkinbayramov.psst.data.dto.VerifyPurchaseRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class BoostRepository {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun getStatus(token: String): Result<BoostStatus> {
        return try {
            val response =
                    client.get("$baseUrl/api/boost/status") {
                        header("Authorization", "Bearer $token")
                    }

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get boost status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Hands the store receipt to the backend, which decides whether a boost is granted. The
     * response is the authoritative new status.
     */
    suspend fun verifyPurchase(
            request: VerifyPurchaseRequest,
            token: String
    ): Result<BoostStatus> {
        return try {
            val response =
                    client.post("$baseUrl/api/boost/verify") {
                        header("Authorization", "Bearer $token")
                        contentType(ContentType.Application.Json)
                        setBody(request)
                    }

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Purchase verification failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
