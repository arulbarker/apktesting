package com.vapestoreunik.madep.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kasir_prefs")

data class StoreProfile(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val logoUri: String? = null,
)

data class TaxConfig(val enabled: Boolean = false, val percent: Int = 0)

interface AppPreferences {
    val storeProfile: Flow<StoreProfile>
    val taxConfig: Flow<TaxConfig>
    val receiptFooter: Flow<String>
    val setupCompleted: Flow<Boolean>
    val pinHash: Flow<String?>
    val failedAttempts: Flow<Int>
    val lockoutUntil: Flow<Long>

    suspend fun updateStoreProfile(p: StoreProfile)
    suspend fun updateTax(t: TaxConfig)
    suspend fun updateReceiptFooter(footer: String)
    suspend fun setSetupCompleted(value: Boolean)
    suspend fun setPinHash(hash: String)
    suspend fun setFailedAttempts(n: Int)
    suspend fun setLockoutUntil(epochMillis: Long)
    suspend fun clearAll()
}

class DataStoreAppPreferences(private val context: Context) : AppPreferences {
    private object K {
        val storeName = stringPreferencesKey("store.name")
        val storeAddress = stringPreferencesKey("store.address")
        val storePhone = stringPreferencesKey("store.phone")
        val storeLogo = stringPreferencesKey("store.logoUri")
        val taxEnabled = booleanPreferencesKey("tax.enabled")
        val taxPercent = intPreferencesKey("tax.percent")
        val receiptFooter = stringPreferencesKey("receipt.footer")
        val setupCompleted = booleanPreferencesKey("setup.completed")
        val pinHash = stringPreferencesKey("auth.pinHash")
        val failedAttempts = intPreferencesKey("auth.failedAttempts")
        val lockoutUntil = longPreferencesKey("auth.lockoutUntil")
    }

    override val storeProfile: Flow<StoreProfile> = context.dataStore.data.map {
        StoreProfile(
            name = it[K.storeName].orEmpty(),
            address = it[K.storeAddress].orEmpty(),
            phone = it[K.storePhone].orEmpty(),
            logoUri = it[K.storeLogo],
        )
    }
    override val taxConfig: Flow<TaxConfig> = context.dataStore.data.map {
        TaxConfig(enabled = it[K.taxEnabled] ?: false, percent = it[K.taxPercent] ?: 0)
    }
    override val receiptFooter: Flow<String> = context.dataStore.data.map {
        it[K.receiptFooter]
            ?: "Terima kasih sudah berbelanja!\nTukar/kembali dalam 7 hari dengan struk."
    }
    override val setupCompleted: Flow<Boolean> =
        context.dataStore.data.map { it[K.setupCompleted] ?: false }
    override val pinHash: Flow<String?> = context.dataStore.data.map { it[K.pinHash] }
    override val failedAttempts: Flow<Int> =
        context.dataStore.data.map { it[K.failedAttempts] ?: 0 }
    override val lockoutUntil: Flow<Long> =
        context.dataStore.data.map { it[K.lockoutUntil] ?: 0L }

    override suspend fun updateStoreProfile(p: StoreProfile) {
        context.dataStore.edit {
            it[K.storeName] = p.name
            it[K.storeAddress] = p.address
            it[K.storePhone] = p.phone
            if (p.logoUri != null) it[K.storeLogo] = p.logoUri else it.remove(K.storeLogo)
        }
    }

    override suspend fun updateTax(t: TaxConfig) {
        context.dataStore.edit {
            it[K.taxEnabled] = t.enabled
            it[K.taxPercent] = t.percent
        }
    }

    override suspend fun updateReceiptFooter(footer: String) {
        context.dataStore.edit { it[K.receiptFooter] = footer }
    }

    override suspend fun setSetupCompleted(value: Boolean) {
        context.dataStore.edit { it[K.setupCompleted] = value }
    }

    override suspend fun setPinHash(hash: String) {
        context.dataStore.edit { it[K.pinHash] = hash }
    }

    override suspend fun setFailedAttempts(n: Int) {
        context.dataStore.edit { it[K.failedAttempts] = n }
    }

    override suspend fun setLockoutUntil(epochMillis: Long) {
        context.dataStore.edit { it[K.lockoutUntil] = epochMillis }
    }

    override suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
