package com.vapestoreunik.madep.domain.usecase

import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.domain.model.Cart
import com.vapestoreunik.madep.domain.model.CartTotals
import com.vapestoreunik.madep.domain.model.DiscountInput
import com.vapestoreunik.madep.domain.model.DiscountType
import javax.inject.Inject

class CalculateTotalsUseCase @Inject constructor() {

    /**
     * subtotal → diskon → pajak → total → kembalian.
     * paid = 0 berarti belum input pembayaran (tidak melempar PaymentInsufficient kalau total juga 0).
     */
    fun execute(cart: Cart, taxPercent: Int, paid: Long): Result<CartTotals> {
        if (cart.items.isEmpty()) return Result.failure(KasirException.CartEmpty())
        val subtotal = cart.rawSubtotal
        val discountAmount = calcDiscount(subtotal, cart.discount)
        if (discountAmount > subtotal) return Result.failure(KasirException.DiscountExceedsSubtotal())
        val afterDiscount = subtotal - discountAmount
        val taxAmount = if (taxPercent > 0) roundHalfUp(afterDiscount * taxPercent.toLong(), 100L) else 0L
        val total = afterDiscount + taxAmount
        if (paid in 1 until total) return Result.failure(KasirException.PaymentInsufficient(total, paid))
        val change = if (paid >= total) paid - total else 0L
        return Result.success(CartTotals(subtotal, discountAmount, taxAmount, total, change))
    }

    private fun calcDiscount(subtotal: Long, d: DiscountInput?): Long {
        if (d == null) return 0L
        return when (d.type) {
            DiscountType.NOMINAL -> d.value.coerceAtLeast(0)
            DiscountType.PERCENT -> {
                val pct = d.value.coerceIn(0, 100)
                if (pct == 100L) subtotal else roundHalfUp(subtotal * pct, 100L)
            }
        }
    }

    /** integer half-up rounding: (numerator + denom/2) / denom (positive values only) */
    private fun roundHalfUp(numerator: Long, denom: Long): Long =
        (numerator + denom / 2) / denom
}
