package com.vapestoreunik.madep.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("""
        SELECT * FROM products
        WHERE isActive = 1
          AND (:query = '' OR name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%')
          AND (:categoryId IS NULL OR categoryId = :categoryId)
        ORDER BY name
    """)
    fun observeFiltered(query: String, categoryId: Long?): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id")
    fun observeById(id: Long): Flow<ProductEntity?>

    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Query("UPDATE products SET isActive = :active, updatedAt = :now WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean, now: Long)

    @Query("""
        SELECT COUNT(*) FROM transaction_items ti
        INNER JOIN product_variants v ON ti.variantId = v.id
        WHERE v.productId = :id
    """)
    suspend fun transactionCountFor(id: Long): Int

    @Delete
    suspend fun delete(product: ProductEntity)
}
