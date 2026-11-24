package com.ilkinbayramov.psst.billing

import kotlin.random.Random

/** What the store hands back after a successful purchase. */
data class StorePurchase(val productId: String, val transactionId: String)

sealed class BillingOutcome {
    data class Success(val purchase: StorePurchase) : BillingOutcome()

    data object Cancelled : BillingOutcome()

    data class Failed(val message: String) : BillingOutcome()
}

/**
 * The seam between the UI and the app store. Swapping [FakeBillingClient] for the RevenueCat
 * implementation is the only change the UI will need once the store accounts are ready.
 */
interface BillingClient {
    suspend fun purchase(productId: String): BillingOutcome
}

/**
 * Pretends the purchase succeeded so the end-to-end flow can be tested without store accounts.
 * The backend must be running with BILLING_MODE=dev for the receipt it produces to be accepted.
 */
class FakeBillingClient : BillingClient {
    override suspend fun purchase(productId: String): BillingOutcome {
        val transactionId = "dev-${Random.nextLong(1_000_000_000L, 9_999_999_999L)}"
        println("⚠️ BILLING: fake purchase $productId -> $transactionId")
        return BillingOutcome.Success(StorePurchase(productId, transactionId))
    }
}

// TODO: return a RevenueCatBillingClient once the Play Console products exist
fun createBillingClient(): BillingClient = FakeBillingClient()
