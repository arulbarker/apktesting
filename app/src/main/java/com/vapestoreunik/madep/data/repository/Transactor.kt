package com.vapestoreunik.madep.data.repository

import androidx.room.withTransaction
import com.vapestoreunik.madep.data.local.KasirDatabase
import javax.inject.Inject

/**
 * Abstraction over Room's `db.withTransaction { ... }` so business logic
 * (CheckoutUseCase) can be unit-tested with a fake `NoopTransactor` without
 * needing a real Room database in src/test.
 */
interface Transactor {
    suspend fun <R> withTx(block: suspend () -> R): R
}

class RoomTransactor @Inject constructor(
    private val db: KasirDatabase,
) : Transactor {
    override suspend fun <R> withTx(block: suspend () -> R): R = db.withTransaction { block() }
}
