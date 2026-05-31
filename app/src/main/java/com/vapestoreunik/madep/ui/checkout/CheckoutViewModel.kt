package com.vapestoreunik.madep.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.preferences.TaxConfig
import com.vapestoreunik.madep.domain.model.Cart
import com.vapestoreunik.madep.domain.model.CartTotals
import com.vapestoreunik.madep.domain.model.DiscountInput
import com.vapestoreunik.madep.domain.model.PaymentInput
import com.vapestoreunik.madep.domain.model.PaymentMethod
import com.vapestoreunik.madep.domain.usecase.CalculateTotalsUseCase
import com.vapestoreunik.madep.domain.usecase.CheckoutUseCase
import com.vapestoreunik.madep.ui.pos.CartHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CheckoutState(
    val cart: Cart = Cart(),
    val discount: DiscountInput? = null,
    val method: PaymentMethod = PaymentMethod.TUNAI,
    val paid: Long = 0L,
    val note: String = "",
    val totals: CartTotals? = null,
    val error: String? = null,
    val processing: Boolean = false,
    val resultTransactionId: Long? = null,
    val taxPercent: Int = 0,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartHolder: CartHolder,
    private val calculate: CalculateTotalsUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    prefs: AppPreferences,
) : ViewModel() {
    private val _discount = MutableStateFlow<DiscountInput?>(null)
    private val _method = MutableStateFlow(PaymentMethod.TUNAI)
    private val _paid = MutableStateFlow(0L)
    private val _note = MutableStateFlow("")
    private val _result = MutableStateFlow<Long?>(null)
    private val _error = MutableStateFlow<String?>(null)
    private val _processing = MutableStateFlow(false)

    val state: StateFlow<CheckoutState> = combine(
        listOf(
            cartHolder.cart, _discount, _method, _paid, _note,
            prefs.taxConfig, _result, _error, _processing,
        ),
    ) { arr ->
        @Suppress("UNCHECKED_CAST")
        val cart = (arr[0] as Cart).copy(
            discount = arr[1] as DiscountInput?,
            note = (arr[4] as String).ifBlank { null },
        )
        val tax = arr[5] as TaxConfig
        val taxPct = if (tax.enabled) tax.percent else 0
        val totals = calculate.execute(cart, taxPct, arr[3] as Long).getOrNull()
        CheckoutState(
            cart = cart,
            discount = arr[1] as DiscountInput?,
            method = arr[2] as PaymentMethod,
            paid = arr[3] as Long,
            note = arr[4] as String,
            totals = totals,
            error = arr[7] as String?,
            processing = arr[8] as Boolean,
            resultTransactionId = arr[6] as Long?,
            taxPercent = taxPct,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CheckoutState())

    fun setDiscount(d: DiscountInput?) { _discount.value = d }

    fun setMethod(m: PaymentMethod) {
        _method.value = m
        if (m != PaymentMethod.TUNAI) _paid.value = state.value.totals?.total ?: 0L
    }

    fun setPaid(amount: Long) { _paid.value = amount }
    fun setNote(n: String) { _note.value = n }

    fun submit() = viewModelScope.launch {
        val s = state.value
        if (s.totals == null) {
            _error.value = "Cek input pembayaran"
            return@launch
        }
        _processing.value = true
        checkoutUseCase.execute(s.cart, PaymentInput(s.method, s.paid), s.taxPercent)
            .onSuccess { id ->
                _result.value = id
                cartHolder.reset()
            }
            .onFailure { _error.value = it.message }
        _processing.value = false
    }

    fun clearError() { _error.value = null }
}
