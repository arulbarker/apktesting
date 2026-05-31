package com.vapestoreunik.madep.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

/**
 * Helper untuk DAO test — in-memory Room, allow main thread queries supaya
 * test runner gampang.
 */
fun buildInMemoryDb(): KasirDatabase {
    val ctx = ApplicationProvider.getApplicationContext<Context>()
    return Room.inMemoryDatabaseBuilder(ctx, KasirDatabase::class.java)
        .allowMainThreadQueries()
        .build()
}
