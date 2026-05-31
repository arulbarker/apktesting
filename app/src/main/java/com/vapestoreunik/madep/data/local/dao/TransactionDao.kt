package com.vapestoreunik.madep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.local.entity.TransactionItemEntity
import kotlinx.coroutines.flow.Flow

data class PaymentSum(val paymentMethod: String, val sum: Long)
data class TopProductRow(val productName: String, val variantName: String, val qty: Int, val omzet: Long)

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(tx: TransactionEntity): Long

    @Insert
    suspend fun insertItems(items: List<TransactionItemEntity>)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun itemsOf(transactionId: Long): List<TransactionItemEntity>

    @Query("""
        SELECT * FROM transactions
        WHERE createdAt BETWEEN :fromMillis AND :toMillis
          AND (:method IS NULL OR paymentMethod = :method)
        ORDER BY createdAt DESC
    """)
    fun observeRange(fromMillis: Long, toMillis: Long, method: String?): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE createdAt BETWEEN :fromMillis AND :toMillis")
    suspend fun countInRange(fromMillis: Long, toMillis: Long): Int

    @Query("SELECT COALESCE(SUM(total), 0) FROM transactions WHERE createdAt BETWEEN :fromMillis AND :toMillis")
    suspend fun omzetInRange(fromMillis: Long, toMillis: Long): Long

    @Query("""
        SELECT paymentMethod, COALESCE(SUM(total), 0) AS sum
        FROM transactions
        WHERE createdAt BETWEEN :fromMillis AND :toMillis
        GROUP BY paymentMethod
    """)
    suspend fun omzetByMethod(fromMillis: Long, toMillis: Long): List<PaymentSum>

    @Query("""
        SELECT ti.variantNameSnapshot AS variantName,
               ti.productNameSnapshot AS productName,
               SUM(ti.qty) AS qty,
               SUM(ti.subtotal) AS omzet
        FROM transaction_items ti
        INNER JOIN transactions t ON ti.transactionId = t.id
        WHERE t.createdAt BETWEEN :fromMillis AND :toMillis
        GROUP BY ti.variantId
        ORDER BY qty DESC
        LIMIT :limit
    """)
    suspend fun topProducts(fromMillis: Long, toMillis: Long, limit: Int): List<TopProductRow>

    @Query("SELECT COUNT(*) FROM transactions WHERE code LIKE :pattern")
    suspend fun countByCodePattern(pattern: String): Int
}
