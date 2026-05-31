package com.vapestoreunik.madep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.RESTRICT,
    )],
    indices = [Index("categoryId")],
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: Long,
    val brand: String? = null,
    val imageUri: String? = null,
    val description: String? = null,
    val hasVariants: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long,
)
