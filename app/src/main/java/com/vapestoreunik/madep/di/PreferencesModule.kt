package com.vapestoreunik.madep.di

import android.content.Context
import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.preferences.DataStoreAppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext ctx: Context): AppPreferences =
        DataStoreAppPreferences(ctx)
}
