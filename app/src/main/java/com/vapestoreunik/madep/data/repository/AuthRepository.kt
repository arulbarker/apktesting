package com.vapestoreunik.madep.data.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.preferences.AppPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.first

interface AuthRepository {
    suspend fun isPinSet(): Boolean
    suspend fun setPin(pin: String)
    suspend fun verifyPin(pin: String): Result<Unit>
    suspend fun clearLockout()
    suspend fun lockoutRemainingSeconds(): Int
}

class DefaultAuthRepository @Inject constructor(
    private val prefs: AppPreferences,
    private val clock: () -> Long = System::currentTimeMillis,
) : AuthRepository {

    companion object {
        const val MAX_ATTEMPTS = 5
        const val LOCKOUT_SECONDS = 30
        const val BCRYPT_COST = 10
    }

    override suspend fun isPinSet(): Boolean = !prefs.pinHash.first().isNullOrBlank()

    override suspend fun setPin(pin: String) {
        require(pin.length in 4..6 && pin.all { it.isDigit() }) {
            "PIN harus 4-6 digit angka"
        }
        val hash = BCrypt.withDefaults().hashToString(BCRYPT_COST, pin.toCharArray())
        prefs.setPinHash(hash)
        prefs.setFailedAttempts(0)
        prefs.setLockoutUntil(0L)
    }

    override suspend fun verifyPin(pin: String): Result<Unit> {
        val now = clock()
        val lockUntil = prefs.lockoutUntil.first()
        if (lockUntil > now) {
            val remain = ((lockUntil - now + 999) / 1000).toInt()
            return Result.failure(KasirException.PinLockedOut(remain))
        }
        val hash = prefs.pinHash.first()
            ?: return Result.failure(IllegalStateException("PIN belum diatur"))
        val ok = BCrypt.verifyer().verify(pin.toCharArray(), hash).verified
        return if (ok) {
            prefs.setFailedAttempts(0)
            prefs.setLockoutUntil(0L)
            Result.success(Unit)
        } else {
            val attempts = prefs.failedAttempts.first() + 1
            prefs.setFailedAttempts(attempts)
            if (attempts >= MAX_ATTEMPTS) {
                prefs.setLockoutUntil(now + LOCKOUT_SECONDS * 1000L)
                prefs.setFailedAttempts(0)
                Result.failure(KasirException.PinLockedOut(LOCKOUT_SECONDS))
            } else {
                Result.failure(KasirException.InvalidPin(MAX_ATTEMPTS - attempts))
            }
        }
    }

    override suspend fun clearLockout() {
        prefs.setFailedAttempts(0)
        prefs.setLockoutUntil(0L)
    }

    override suspend fun lockoutRemainingSeconds(): Int {
        val until = prefs.lockoutUntil.first()
        val now = clock()
        return if (until > now) ((until - now + 999) / 1000).toInt() else 0
    }
}
