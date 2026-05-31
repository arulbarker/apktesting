package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.data.local.dao.PaymentSum
import com.vapestoreunik.madep.data.local.dao.TopProductRow
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.local.entity.TransactionItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeTransactionRepository(
    private val nextSeqProvider: (String) -> Int = { 1 },
) : TransactionRepository {

    val daysQueried = mutableListOf<String>()

    override fun observeRange(
        fromMillis: Long,
        toMillis: Long,
        method: String?,
    ): Flow<List<TransactionEntity>> = emptyFlow()

    override suspend fun getById(id: Long): TransactionEntity? = null
    override suspend fun itemsOf(id: Long): List<TransactionItemEntity> = emptyList()
    override suspend fun omzetInRange(fromMillis: Long, toMillis: Long): Long = 0
    override suspend fun countInRange(fromMillis: Long, toMillis: Long): Int = 0
    override suspend fun omzetByMethod(fromMillis: Long, toMillis: Long): List<PaymentSum> =
        emptyList()

    override suspend fun topProducts(
        fromMillis: Long,
        toMillis: Long,
        limit: Int,
    ): List<TopProductRow> = emptyList()

    override suspend fun nextSequenceForDay(yyyymmdd: String): Int {
        daysQueried.add(yyyymmdd)
        return nextSeqProvider(yyyymmdd)
    }
}

class NoopTransactor : Transactor {
    override suspend fun <R> withTx(block: suspend () -> R): R = block()
}
