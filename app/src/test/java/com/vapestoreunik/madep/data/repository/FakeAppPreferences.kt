package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.preferences.StoreProfile
import com.vapestoreunik.madep.data.preferences.TaxConfig
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAppPreferences : AppPreferences {
    private val _storeProfile = MutableStateFlow(StoreProfile())
    private val _tax = MutableStateFlow(TaxConfig())
    private val _footer = MutableStateFlow("Terima kasih sudah berbelanja!")
    private val _setupCompleted = MutableStateFlow(false)
    private val _pinHash = MutableStateFlow<String?>(null)
    private val _failedAttempts = MutableStateFlow(0)
    private val _lockoutUntil = MutableStateFlow(0L)

    override val storeProfile = _storeProfile
    override val taxConfig = _tax
    override val receiptFooter = _footer
    override val setupCompleted = _setupCompleted
    override val pinHash = _pinHash
    override val failedAttempts = _failedAttempts
    override val lockoutUntil = _lockoutUntil

    override suspend fun updateStoreProfile(p: StoreProfile) {
        _storeProfile.value = p
    }

    override suspend fun updateTax(t: TaxConfig) {
        _tax.value = t
    }

    override suspend fun updateReceiptFooter(footer: String) {
        _footer.value = footer
    }

    override suspend fun setSetupCompleted(value: Boolean) {
        _setupCompleted.value = value
    }

    override suspend fun setPinHash(hash: String) {
        _pinHash.value = hash
    }

    override suspend fun setFailedAttempts(n: Int) {
        _failedAttempts.value = n
    }

    override suspend fun setLockoutUntil(epochMillis: Long) {
        _lockoutUntil.value = epochMillis
    }

    override suspend fun clearAll() {
        _storeProfile.value = StoreProfile()
        _tax.value = TaxConfig()
        _setupCompleted.value = false
        _pinHash.value = null
        _failedAttempts.value = 0
        _lockoutUntil.value = 0
    }
}
