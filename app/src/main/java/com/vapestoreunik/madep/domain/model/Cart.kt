package com.vapestoreunik.madep.domain.model

data class CartItem(
    val variantId: Long,
    val productName: String,
    val variantName: String,
    val unitPrice: Long,
    val qty: Int,
    val availableStock: Int,
) {
    val subtotal: Long get() = unitPrice * qty
}

enum class DiscountType { PERCENT, NOMINAL }

data class DiscountInput(val type: DiscountType, val value: Long)

enum class PaymentMethod(val raw: String) {
    TUNAI("TUNAI"),
    NON_TUNAI("NON_TUNAI");

    companion object {
        fun fromRaw(s: String): PaymentMethod = entries.first { it.raw == s }
    }
}

data class PaymentInput(val method: PaymentMethod, val paid: Long)

data class CartTotals(
    val subtotal: Long,
    val discountAmount: Long,
    val taxAmount: Long,
    val total: Long,
    val change: Long,
)

data class Cart(
    val items: List<CartItem> = emptyList(),
    val discount: DiscountInput? = null,
    val note: String? = null,
) {
    fun addItem(item: CartItem): Cart {
        val existing = items.indexOfFirst { it.variantId == item.variantId }
        return if (existing >= 0) {
            val updated = items[existing].copy(qty = items[existing].qty + item.qty)
            copy(items = items.toMutableList().also { it[existing] = updated })
        } else {
            copy(items = items + item)
        }
    }

    fun updateQty(variantId: Long, newQty: Int): Cart {
        val updated = items.mapNotNull {
            if (it.variantId == variantId) {
                if (newQty <= 0) null else it.copy(qty = newQty)
            } else it
        }
        return copy(items = updated)
    }

    fun removeItem(variantId: Long): Cart = copy(items = items.filter { it.variantId != variantId })

    val itemCount: Int get() = items.sumOf { it.qty }
    val rawSubtotal: Long get() = items.sumOf { it.subtotal }
}
