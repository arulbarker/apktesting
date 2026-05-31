package com.vapestoreunik.madep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_logs",
    foreignKeys = [ForeignKey(
        entity = VariantEntity::class,
        parentColumns = ["id"],
        childColumns = ["variantId"],
        onDelete = ForeignKey.RESTRICT,
    )],
    indices = [Index("variantId"), Index("createdAt")],
)
data class StockLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val variantId: Long,
    val type: String,
    val qty: Int,
    val balanceAfter: Int,
    val refType: String? = null,
    val refId: Long? = null,
    val note: String? = null,
    val createdAt: Long,
)
