package com.vapestoreunik.madep.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.preferences.StoreProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class StoreProfileState(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val logoUri: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class StoreProfileViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow(StoreProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val p = prefs.storeProfile.first()
            _state.value = StoreProfileState(p.name, p.address, p.phone, p.logoUri)
        }
    }

    fun setName(v: String) { _state.value = _state.value.copy(name = v, saved = false) }
    fun setAddress(v: String) { _state.value = _state.value.copy(address = v, saved = false) }
    fun setPhone(v: String) { _state.value = _state.value.copy(phone = v, saved = false) }

    fun save() = viewModelScope.launch {
        val s = _state.value
        prefs.updateStoreProfile(
            StoreProfile(name = s.name, address = s.address, phone = s.phone, logoUri = s.logoUri),
        )
        _state.value = s.copy(saved = true)
    }
}
