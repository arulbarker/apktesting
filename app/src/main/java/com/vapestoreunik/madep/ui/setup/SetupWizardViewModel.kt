package com.vapestoreunik.madep.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.preferences.StoreProfile
import com.vapestoreunik.madep.data.repository.AuthRepository
import com.vapestoreunik.madep.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SetupWizardState(
    val step: Int = 0,                // 0=profile, 1=pin, 2=summary
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val pin: String = "",
    val pinConfirm: String = "",
    val error: String? = null,
    val completed: Boolean = false,
)

@HiltViewModel
class SetupWizardViewModel @Inject constructor(
    private val prefs: AppPreferences,
    private val auth: AuthRepository,
    private val categories: CategoryRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SetupWizardState())
    val state = _state.asStateFlow()

    fun setName(v: String) = _state.update { it.copy(name = v, error = null) }
    fun setAddress(v: String) = _state.update { it.copy(address = v) }
    fun setPhone(v: String) = _state.update { it.copy(phone = v) }
    fun setPin(v: String) = _state.update { it.copy(pin = v, error = null) }
    fun setPinConfirm(v: String) = _state.update { it.copy(pinConfirm = v, error = null) }

    fun nextStep() {
        when (_state.value.step) {
            0 -> {
                if (_state.value.name.isBlank()) {
                    _state.update { it.copy(error = "Nama toko wajib diisi") }
                    return
                }
                _state.update { it.copy(step = 1, error = null) }
            }
            1 -> {
                val s = _state.value
                if (s.pin.length !in 4..6 || !s.pin.all(Char::isDigit)) {
                    _state.update { it.copy(error = "PIN harus 4-6 digit angka") }
                    return
                }
                if (s.pin != s.pinConfirm) {
                    _state.update { it.copy(error = "Konfirmasi PIN tidak cocok") }
                    return
                }
                _state.update { it.copy(step = 2, error = null) }
            }
            2 -> finish()
        }
    }

    fun prevStep() = _state.update {
        it.copy(step = (it.step - 1).coerceAtLeast(0), error = null)
    }

    private fun finish() = viewModelScope.launch {
        val s = _state.value
        prefs.updateStoreProfile(
            StoreProfile(name = s.name, address = s.address, phone = s.phone),
        )
        auth.setPin(s.pin)
        categories.seedDefaults()
        prefs.setSetupCompleted(true)
        _state.update { it.copy(completed = true) }
    }
}
