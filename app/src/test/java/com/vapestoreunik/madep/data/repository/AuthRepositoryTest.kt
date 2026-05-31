package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.core.common.KasirException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class AuthRepositoryTest {

    @Test fun set_pin_stores_hash_not_plaintext() = runTest {
        val prefs = FakeAppPreferences()
        val repo = DefaultAuthRepository(prefs) { 0L }
        repo.setPin("1234")
        val hash = prefs.pinHash.first()
        assertNotNull(hash)
        assertNotEquals("1234", hash)
        assertTrue(hash!!.startsWith("\$2"))  // bcrypt prefix
    }

    @Test fun set_pin_invalid_length_throws() = runTest {
        val repo = DefaultAuthRepository(FakeAppPreferences()) { 0L }
        try { repo.setPin("123"); fail("3-digit PIN should fail") } catch (e: IllegalArgumentException) {}
        try { repo.setPin("1234567"); fail("7-digit PIN should fail") } catch (e: IllegalArgumentException) {}
        try { repo.setPin("abcd"); fail("non-digit PIN should fail") } catch (e: IllegalArgumentException) {}
    }

    @Test fun verify_correct_pin_returns_success() = runTest {
        val repo = DefaultAuthRepository(FakeAppPreferences()) { 0L }
        repo.setPin("1234")
        assertTrue(repo.verifyPin("1234").isSuccess)
    }

    @Test fun verify_wrong_pin_returns_failure_with_attempts_left() = runTest {
        val repo = DefaultAuthRepository(FakeAppPreferences()) { 0L }
        repo.setPin("1234")
        val r = repo.verifyPin("9999")
        assertTrue(r.isFailure)
        val e = r.exceptionOrNull() as KasirException.InvalidPin
        assertEquals(4, e.attemptsLeft)
    }

    @Test fun five_failed_attempts_triggers_lockout() = runTest {
        val prefs = FakeAppPreferences()
        val repo = DefaultAuthRepository(prefs) { 0L }
        repo.setPin("1234")
        repeat(5) { repo.verifyPin("9999") }
        // attempts harus reset, lockoutUntil di-set
        assertEquals(0, prefs.failedAttempts.first())
        assertEquals(30_000L, prefs.lockoutUntil.first())
    }

    @Test fun lockout_blocks_further_attempts_until_expired() = runTest {
        val prefs = FakeAppPreferences()
        var clock = 0L
        val repo = DefaultAuthRepository(prefs) { clock }
        repo.setPin("1234")
        repeat(5) { repo.verifyPin("9999") }

        clock = 15_000L  // 15 detik kemudian
        val r = repo.verifyPin("1234")
        assertTrue(r.isFailure)
        val e = r.exceptionOrNull() as KasirException.PinLockedOut
        assertEquals(15, e.remainingSeconds)

        clock = 31_000L  // setelah 30 detik
        assertTrue(repo.verifyPin("1234").isSuccess)
    }
}
