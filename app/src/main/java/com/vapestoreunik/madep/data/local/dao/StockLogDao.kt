package com.vapestoreunik.madep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vapestoreunik.madep.data.local.entity.StockLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockLogDao {
    @Insert
    suspend fun insert(log: StockLogEntity): Long

    @Insert
    suspend fun insertAll(logs: List<StockLogEntity>)

    @Query("SELECT * FROM stock_logs WHERE variantId = :variantId ORDER BY createdAt DESC")
    fun observeByVariant(variantId: Long): Flow<List<StockLogEntity>>
}
