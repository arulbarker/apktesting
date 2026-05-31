package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.data.local.dao.StockLogDao
import com.vapestoreunik.madep.data.local.entity.StockLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeStockLogDao : StockLogDao {
    val logs = mutableListOf<StockLogEntity>()

    override suspend fun insert(log: StockLogEntity): Long {
        logs.add(log)
        return logs.size.toLong()
    }

    override suspend fun insertAll(logs: List<StockLogEntity>) {
        this.logs.addAll(logs)
    }

    override fun observeByVariant(variantId: Long): Flow<List<StockLogEntity>> = emptyFlow()
}
