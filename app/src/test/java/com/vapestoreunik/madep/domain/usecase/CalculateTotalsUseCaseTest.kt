package com.vapestoreunik.madep.domain.usecase

import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.domain.model.Cart
import com.vapestoreunik.madep.domain.model.CartItem
import com.vapestoreunik.madep.domain.model.DiscountInput
import com.vapestoreunik.madep.domain.model.DiscountType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateTotalsUseCaseTest {
    private val uc = CalculateTotalsUseCase()
    private fun item(qty: Int, price: Long) = CartItem(1L, "P", "V", price, qty, 100)

    @Test fun subtotal_sums_items() {
        val cart = Cart(items = listOf(item(2, 25000), item(1, 75000)))
        val totals = uc.execute(cart, taxPercent = 0, paid = 0).getOrThrow()
        assertEquals(125000L, totals.subtotal)
    }

    @Test fun discount_percent_rounds_half_up() {
        // 10% of 12345 = 1234.5 → 1235
        val cart = Cart(items = listOf(item(1, 12345)), discount = DiscountInput(DiscountType.PERCENT, 10))
        val totals = uc.execute(cart, taxPercent = 0, paid = 12345).getOrThrow()
        assertEquals(1235L, totals.discountAmount)
        assertEquals(11110L, totals.total)
    }

    @Test fun discount_percent_capped_at_100() {
        val cart = Cart(items = listOf(item(1, 50000)), discount = DiscountInput(DiscountType.PERCENT, 100))
        val totals = uc.execute(cart, taxPercent = 0, paid = 0).getOrThrow()
        assertEquals(50000L, totals.discountAmount)
        assertEquals(0L, totals.total)
    }

    @Test fun discount_nominal_exceeds_subtotal_throws() {
        val cart = Cart(items = listOf(item(1, 50000)), discount = DiscountInput(DiscountType.NOMINAL, 60000))
        val r = uc.execute(cart, taxPercent = 0, paid = 0)
        assertTrue(r.isFailure)
        assertTrue(r.exceptionOrNull() is KasirException.DiscountExceedsSubtotal)
    }

    @Test fun tax_applied_after_discount() {
        // 100000 - 10000 = 90000; 11% of 90000 = 9900
        val cart = Cart(items = listOf(item(1, 100000)), discount = DiscountInput(DiscountType.NOMINAL, 10000))
        val totals = uc.execute(cart, taxPercent = 11, paid = 99900).getOrThrow()
        assertEquals(10000L, totals.discountAmount)
        assertEquals(9900L, totals.taxAmount)
        assertEquals(99900L, totals.total)
    }

    @Test fun tax_zero_when_disabled() {
        val cart = Cart(items = listOf(item(1, 100000)))
        val totals = uc.execute(cart, taxPercent = 0, paid = 100000).getOrThrow()
        assertEquals(0L, totals.taxAmount)
    }

    @Test fun change_equals_paid_minus_total() {
        val cart = Cart(items = listOf(item(1, 75000)))
        val totals = uc.execute(cart, taxPercent = 0, paid = 100000).getOrThrow()
        assertEquals(25000L, totals.change)
    }

    @Test fun paid_less_than_total_throws() {
        val cart = Cart(items = listOf(item(1, 75000)))
        val r = uc.execute(cart, taxPercent = 0, paid = 50000)
        assertTrue(r.isFailure)
        assertTrue(r.exceptionOrNull() is KasirException.PaymentInsufficient)
    }

    @Test fun empty_cart_throws() {
        val r = uc.execute(Cart(), taxPercent = 0, paid = 0)
        assertTrue(r.isFailure)
        assertTrue(r.exceptionOrNull() is KasirException.CartEmpty)
    }
}
