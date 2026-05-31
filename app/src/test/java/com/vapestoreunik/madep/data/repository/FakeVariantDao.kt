package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.data.local.dao.VariantDao
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeVariantDao(initialStocks: Map<Long, Int> = emptyMap()) : VariantDao {
    private val stocks: MutableMap<Long, Int> = initialStocks.toMutableMap()

    override fun observeByProduct(productId: Long): Flow<List<VariantEntity>> = emptyFlow()
    override suspend fun getAllByProduct(productId: Long): List<VariantEntity> = emptyList()
    override suspend fun getById(id: Long): VariantEntity? = null
    override suspend fun getByIds(ids: List<Long>): List<VariantEntity> = emptyList()
    override fun observeLowStock(limit: Int): Flow<List<VariantEntity>> = emptyFlow()
    override suspend fun insert(variant: VariantEntity): Long = 0L
    override suspend fun update(variant: VariantEntity) {}
    override suspend fun adjustStock(id: Long, delta: Int, now: Long) {
        stocks[id] = (stocks[id] ?: 0) + delta
    }
    override suspend fun stockOf(id: Long): Int? = stocks[id]
    override suspend fun delete(variant: VariantEntity) {}
}
