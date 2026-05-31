package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.data.local.dao.PaymentSum
import com.vapestoreunik.madep.data.local.dao.TopProductRow
import com.vapestoreunik.madep.data.local.dao.TransactionDao
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.local.entity.TransactionItemEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeRange(
        fromMillis: Long,
        toMillis: Long,
        method: String?,
    ): Flow<List<TransactionEntity>>

    suspend fun getById(id: Long): TransactionEntity?
    suspend fun itemsOf(id: Long): List<TransactionItemEntity>
    suspend fun omzetInRange(fromMillis: Long, toMillis: Long): Long
    suspend fun countInRange(fromMillis: Long, toMillis: Long): Int
    suspend fun omzetByMethod(fromMillis: Long, toMillis: Long): List<PaymentSum>
    suspend fun topProducts(
        fromMillis: Long,
        toMillis: Long,
        limit: Int = 10,
    ): List<TopProductRow>

    suspend fun nextSequenceForDay(yyyymmdd: String): Int
}

class DefaultTransactionRepository @Inject constructor(
    private val dao: TransactionDao,
) : TransactionRepository {
    override fun observeRange(fromMillis: Long, toMillis: Long, method: String?) =
        dao.observeRange(fromMillis, toMillis, method)

    override suspend fun getById(id: Long) = dao.getById(id)
    override suspend fun itemsOf(id: Long) = dao.itemsOf(id)
    override suspend fun omzetInRange(fromMillis: Long, toMillis: Long) =
        dao.omzetInRange(fromMillis, toMillis)

    override suspend fun countInRange(fromMillis: Long, toMillis: Long) =
        dao.countInRange(fromMillis, toMillis)

    override suspend fun omzetByMethod(fromMillis: Long, toMillis: Long) =
        dao.omzetByMethod(fromMillis, toMillis)

    override suspend fun topProducts(fromMillis: Long, toMillis: Long, limit: Int) =
        dao.topProducts(fromMillis, toMillis, limit)

    override suspend fun nextSequenceForDay(yyyymmdd: String): Int {
        val pattern = "TRX-$yyyymmdd-%"
        return dao.countByCodePattern(pattern) + 1
    }
}
