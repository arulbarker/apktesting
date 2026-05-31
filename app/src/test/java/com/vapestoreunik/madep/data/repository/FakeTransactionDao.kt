package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.data.local.dao.PaymentSum
import com.vapestoreunik.madep.data.local.dao.TopProductRow
import com.vapestoreunik.madep.data.local.dao.TransactionDao
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.local.entity.TransactionItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeTransactionDao : TransactionDao {
    val insertedTransactions = mutableListOf<TransactionEntity>()
    val insertedItems = mutableListOf<TransactionItemEntity>()
    private var idCounter = 1L

    override suspend fun insertTransaction(tx: TransactionEntity): Long {
        val id = idCounter++
        insertedTransactions.add(tx.copy(id = id))
        return id
    }

    override suspend fun insertItems(items: List<TransactionItemEntity>) {
        insertedItems.addAll(items)
    }

    override suspend fun getById(id: Long) = insertedTransactions.find { it.id == id }
    override suspend fun itemsOf(transactionId: Long) =
        insertedItems.filter { it.transactionId == transactionId }

    override fun observeRange(
        fromMillis: Long,
        toMillis: Long,
        method: String?,
    ): Flow<List<TransactionEntity>> = emptyFlow()

    override suspend fun countInRange(fromMillis: Long, toMillis: Long): Int = 0
    override suspend fun omzetInRange(fromMillis: Long, toMillis: Long): Long = 0
    override suspend fun omzetByMethod(fromMillis: Long, toMillis: Long): List<PaymentSum> =
        emptyList()

    override suspend fun topProducts(
        fromMillis: Long,
        toMillis: Long,
        limit: Int,
    ): List<TopProductRow> = emptyList()

    override suspend fun countByCodePattern(pattern: String): Int = 0
}
