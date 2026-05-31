package com.vapestoreunik.madep.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vapestoreunik.madep.data.local.dao.CategoryDao
import com.vapestoreunik.madep.data.local.dao.ProductDao
import com.vapestoreunik.madep.data.local.dao.StockLogDao
import com.vapestoreunik.madep.data.local.dao.TransactionDao
import com.vapestoreunik.madep.data.local.dao.VariantDao
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.local.entity.StockLogEntity
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.local.entity.TransactionItemEntity
import com.vapestoreunik.madep.data.local.entity.VariantEntity

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        VariantEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class,
        StockLogEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class KasirDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun variantDao(): VariantDao
    abstract fun transactionDao(): TransactionDao
    abstract fun stockLogDao(): StockLogDao

    companion object {
        const val DB_NAME = "kasir.db"
    }
}
