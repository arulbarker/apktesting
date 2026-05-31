package com.vapestoreunik.madep.di

import android.content.Context
import androidx.room.Room
import com.vapestoreunik.madep.data.local.KasirDatabase
import com.vapestoreunik.madep.data.local.dao.CategoryDao
import com.vapestoreunik.madep.data.local.dao.ProductDao
import com.vapestoreunik.madep.data.local.dao.StockLogDao
import com.vapestoreunik.madep.data.local.dao.TransactionDao
import com.vapestoreunik.madep.data.local.dao.VariantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): KasirDatabase =
        Room.databaseBuilder(ctx, KasirDatabase::class.java, KasirDatabase.DB_NAME)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides
    fun provideCategoryDao(db: KasirDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideProductDao(db: KasirDatabase): ProductDao = db.productDao()

    @Provides
    fun provideVariantDao(db: KasirDatabase): VariantDao = db.variantDao()

    @Provides
    fun provideTransactionDao(db: KasirDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideStockLogDao(db: KasirDatabase): StockLogDao = db.stockLogDao()
}
