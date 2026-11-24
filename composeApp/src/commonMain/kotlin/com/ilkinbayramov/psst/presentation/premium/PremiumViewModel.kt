package com.ilkinbayramov.psst.presentation.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilkinbayramov.psst.billing.BillingClient
import com.ilkinbayramov.psst.billing.BillingOutcome
import com.ilkinbayramov.psst.data.dto.BoostStatus
import com.ilkinbayramov.psst.data.dto.VerifyPurchaseRequest
import com.ilkinbayramov.psst.data.repository.BoostRepository
import com.ilkinbayramov.psst.utils.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PremiumUiState(
        val isLoading: Boolean = true,
        val isPurchasing: Boolean = false,
        val boost: BoostStatus = BoostStatus(),
        /** Ticks down locally between server refreshes so the timer stays smooth. */
        val remainingSeconds: Long = 0,
        val selectedProductId: String = "boost_15",
        val error: String? = null
)

class PremiumViewModel(
        private val boostRepository: BoostRepository,
        private val billingClient: BillingClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremiumUiState())
    val uiState: StateFlow<PremiumUiState> = _uiState.asStateFlow()

    init {
        loadStatus()
        startTicker()
    }

    fun selectProduct(productId: String) {
        _uiState.value = _uiState.value.copy(selectedProductId = productId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun loadStatus() {
        viewModelScope.launch {
            val token = TokenManager.getToken()
            if (token == null) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }

            boostRepository
                    .getStatus(token)
                    .onSuccess { status ->
                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        boost = status,
                                        remainingSeconds = status.remainingSeconds
                                )
                    }
                    .onFailure { _uiState.value = _uiState.value.copy(isLoading = false) }
        }
    }

    fun purchaseSelected() {
        val productId = _uiState.value.selectedProductId
        if (_uiState.value.isPurchasing) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPurchasing = true, error = null)

            when (val outcome = billingClient.purchase(productId)) {
                is BillingOutcome.Success -> verify(outcome.purchase.productId, outcome.purchase.transactionId)
                BillingOutcome.Cancelled ->
                        _uiState.value = _uiState.value.copy(isPurchasing = false)
                is BillingOutcome.Failed ->
                        _uiState.value =
                                _uiState.value.copy(isPurchasing = false, error = outcome.message)
            }
        }
    }

    private suspend fun verify(productId: String, transactionId: String) {
        val token = TokenManager.getToken()
        if (token == null) {
            _uiState.value = _uiState.value.copy(isPurchasing = false)
            return
        }

        boostRepository
                .verifyPurchase(
                        VerifyPurchaseRequest(productId = productId, transactionId = transactionId),
                        token
                )
                .onSuccess { status ->
                    _uiState.value =
                            _uiState.value.copy(
                                    isPurchasing = false,
                                    boost = status,
                                    remainingSeconds = status.remainingSeconds
                            )
                }
                .onFailure { error ->
                    _uiState.value =
                            _uiState.value.copy(isPurchasing = false, error = error.message)
                }
    }

    /**
     * Counts down between refreshes and re-syncs with the server when the boost should have ended,
     * so a wrong device clock can never keep the timer alive.
     */
    private fun startTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _uiState.value
                if (!state.boost.isActive) continue

                val next = state.remainingSeconds - 1
                if (next <= 0) {
                    _uiState.value = state.copy(remainingSeconds = 0)
                    loadStatus()
                } else {
                    _uiState.value = state.copy(remainingSeconds = next)
                }
            }
        }
    }
}
