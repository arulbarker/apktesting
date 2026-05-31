package com.vapestoreunik.madep.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["code"], unique = true),
        Index("createdAt"),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val subtotal: Long,
    val discountType: String? = null,
    val discountValue: Long = 0,
    val discountAmount: Long = 0,
    val taxPercent: Int = 0,
    val taxAmount: Long = 0,
    val total: Long,
    val paid: Long,
    val change: Long,
    val paymentMethod: String,
    val note: String? = null,
    val createdAt: Long,
)
