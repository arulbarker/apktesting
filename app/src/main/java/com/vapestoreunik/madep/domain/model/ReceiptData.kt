package com.vapestoreunik.madep.domain.model

data class ReceiptLine(
    val productName: String,
    val variantName: String,
    val qty: Int,
    val unitPrice: Long,
    val subtotal: Long,
)

data class ReceiptData(
    val storeName: String,
    val storeAddress: String,
    val storePhone: String,
    val transactionCode: String,
    val createdAtMillis: Long,
    val lines: List<ReceiptLine>,
    val subtotal: Long,
    val discountAmount: Long,
    val taxAmount: Long,
    val total: Long,
    val paymentMethod: String,
    val paid: Long,
    val change: Long,
    val footer: String,
)
