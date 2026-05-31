package com.vapestoreunik.madep.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt tidak bisa inject lambda/function type `() -> Long` lewat default value.
 * Provider explicit supaya AuthRepository + CheckoutUseCase bisa inject clock.
 */
@Module
@InstallIn(SingletonComponent::class)
object ClockModule {
    @Provides
    @Singleton
    fun provideClock(): () -> Long = { System.currentTimeMillis() }
}
