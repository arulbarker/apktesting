package com.vapestoreunik.madep.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.preferences.TaxConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsState(
    val storeName: String = "",
    val taxEnabled: Boolean = false,
    val taxPercent: Int = 0,
    val receiptFooter: String = "",
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {
    val state: StateFlow<SettingsState> = combine(
        prefs.storeProfile, prefs.taxConfig, prefs.receiptFooter,
    ) { p, t, f -> SettingsState(p.name, t.enabled, t.percent, f) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun setTaxEnabled(enabled: Boolean) = viewModelScope.launch {
        prefs.updateTax(TaxConfig(enabled = enabled, percent = state.value.taxPercent))
    }

    fun setTaxPercent(percent: Int) = viewModelScope.launch {
        prefs.updateTax(TaxConfig(enabled = state.value.taxEnabled, percent = percent.coerceIn(0, 100)))
    }

    fun setFooter(text: String) = viewModelScope.launch { prefs.updateReceiptFooter(text) }
}
