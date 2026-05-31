package com.vapestoreunik.madep.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChangePinState(
    val step: Int = 0,           // 0=verify lama, 1=PIN baru, 2=konfirmasi
    val oldPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val error: String? = null,
    val done: Boolean = false,
)

@HiltViewModel
class ChangePinViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ChangePinState())
    val state = _state.asStateFlow()

    fun appendDigit(d: Int) {
        val current = _state.value
        when (current.step) {
            0 -> {
                val p = (current.oldPin + d).take(6)
                _state.value = current.copy(oldPin = p, error = null)
                if (p.length in 4..6) verifyOld(p)
            }
            1 -> {
                val p = (current.newPin + d).take(6)
                _state.value = current.copy(newPin = p, error = null)
                if (p.length >= 4) advanceToConfirm(p)
            }
            2 -> {
                val p = (current.confirmPin + d).take(6)
                _state.value = current.copy(confirmPin = p, error = null)
                if (p.length >= 4) submit()
            }
        }
    }

    fun backspace() {
        val current = _state.value
        _state.value = when (current.step) {
            0 -> current.copy(oldPin = current.oldPin.dropLast(1), error = null)
            1 -> current.copy(newPin = current.newPin.dropLast(1), error = null)
            else -> current.copy(confirmPin = current.confirmPin.dropLast(1), error = null)
        }
    }

    private fun verifyOld(pin: String) = viewModelScope.launch {
        auth.verifyPin(pin)
            .onSuccess { _state.value = _state.value.copy(step = 1, oldPin = "") }
            .onFailure { e -> _state.value = _state.value.copy(oldPin = "", error = e.message) }
    }

    private fun advanceToConfirm(pin: String) {
        if (pin.length !in 4..6) {
            _state.value = _state.value.copy(error = "PIN harus 4-6 digit")
            return
        }
        _state.value = _state.value.copy(step = 2)
    }

    private fun submit() = viewModelScope.launch {
        val s = _state.value
        if (s.newPin != s.confirmPin) {
            _state.value = s.copy(confirmPin = "", error = "Konfirmasi tidak cocok")
            return@launch
        }
        runCatching { auth.setPin(s.newPin) }
            .onSuccess { _state.value = s.copy(done = true) }
            .onFailure { _state.value = s.copy(error = it.message ?: "Gagal menyimpan") }
    }
}
