package com.vapestoreunik.madep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_variants",
    foreignKeys = [ForeignKey(
        entity = ProductEntity::class,
        parentColumns = ["id"],
        childColumns = ["productId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [
        Index("productId"),
        Index(value = ["sku"], unique = true),
        Index(value = ["barcode"], unique = true),
    ],
)
data class VariantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val name: String,
    val sku: String? = null,
    val barcode: String? = null,
    val price: Long,
    val cost: Long? = null,
    val stock: Int = 0,
    val lowStockThreshold: Int = 5,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long,
)
