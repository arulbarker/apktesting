package com.vapestoreunik.madep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_items",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = VariantEntity::class,
            parentColumns = ["id"],
            childColumns = ["variantId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("transactionId"), Index("variantId")],
)
data class TransactionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val variantId: Long,
    val productNameSnapshot: String,
    val variantNameSnapshot: String,
    val priceSnapshot: Long,
    val qty: Int,
    val subtotal: Long,
)
