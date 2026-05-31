package com.vapestoreunik.madep.di

import com.vapestoreunik.madep.data.repository.AuthRepository
import com.vapestoreunik.madep.data.repository.CategoryRepository
import com.vapestoreunik.madep.data.repository.DefaultAuthRepository
import com.vapestoreunik.madep.data.repository.DefaultCategoryRepository
import com.vapestoreunik.madep.data.repository.DefaultProductRepository
import com.vapestoreunik.madep.data.repository.DefaultTransactionRepository
import com.vapestoreunik.madep.data.repository.ProductRepository
import com.vapestoreunik.madep.data.repository.RoomTransactor
import com.vapestoreunik.madep.data.repository.Transactor
import com.vapestoreunik.madep.data.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: DefaultCategoryRepository): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: DefaultProductRepository): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: DefaultAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: DefaultTransactionRepository): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindTransactor(impl: RoomTransactor): Transactor
}
