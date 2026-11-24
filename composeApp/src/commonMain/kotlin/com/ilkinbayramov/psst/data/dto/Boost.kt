package com.ilkinbayramov.psst.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BoostStatus(
        val isActive: Boolean = false,
        val productId: String? = null,
        val expiresAt: Long? = null,
        /** Seconds left according to the server, which is the clock we trust. */
        val remainingSeconds: Long = 0
)

@Serializable
data class VerifyPurchaseRequest(
        val productId: String,
        val transactionId: String,
        val platform: String = "android"
)
