package com.vapestoreunik.madep.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VariantDao {
    @Query("SELECT * FROM product_variants WHERE productId = :productId AND isActive = 1 ORDER BY name")
    fun observeByProduct(productId: Long): Flow<List<VariantEntity>>

    @Query("SELECT * FROM product_variants WHERE productId = :productId")
    suspend fun getAllByProduct(productId: Long): List<VariantEntity>

    @Query("SELECT * FROM product_variants WHERE id = :id")
    suspend fun getById(id: Long): VariantEntity?

    @Query("SELECT * FROM product_variants WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<VariantEntity>

    @Query("SELECT * FROM product_variants WHERE isActive = 1 AND stock <= lowStockThreshold ORDER BY stock LIMIT :limit")
    fun observeLowStock(limit: Int): Flow<List<VariantEntity>>

    @Insert
    suspend fun insert(variant: VariantEntity): Long

    @Update
    suspend fun update(variant: VariantEntity)

    @Query("UPDATE product_variants SET stock = stock + :delta, updatedAt = :now WHERE id = :id")
    suspend fun adjustStock(id: Long, delta: Int, now: Long)

    @Query("SELECT stock FROM product_variants WHERE id = :id")
    suspend fun stockOf(id: Long): Int?

    @Delete
    suspend fun delete(variant: VariantEntity)
}
