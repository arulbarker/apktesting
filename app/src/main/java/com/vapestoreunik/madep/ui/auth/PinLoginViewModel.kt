package com.vapestoreunik.madep.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PinLoginState(
    val pin: String = "",
    val error: String? = null,
    val lockoutSeconds: Int = 0,
    val unlocked: Boolean = false,
)

@HiltViewModel
class PinLoginViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PinLoginState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val s = auth.lockoutRemainingSeconds()
            if (s > 0) startLockoutCountdown(s)
        }
    }

    fun appendDigit(d: Int) {
        if (_state.value.lockoutSeconds > 0) return
        val newPin = (_state.value.pin + d).take(6)
        _state.value = _state.value.copy(pin = newPin, error = null)
        if (newPin.length >= 4) tryVerify(newPin)
    }

    fun backspace() {
        if (_state.value.lockoutSeconds > 0) return
        _state.value = _state.value.copy(pin = _state.value.pin.dropLast(1), error = null)
    }

    private fun tryVerify(pin: String) = viewModelScope.launch {
        auth.verifyPin(pin)
            .onSuccess { _state.value = _state.value.copy(unlocked = true, pin = "") }
            .onFailure { e ->
                _state.value = _state.value.copy(pin = "", error = e.message)
                if (e is KasirException.PinLockedOut) startLockoutCountdown(e.remainingSeconds)
            }
    }

    private fun startLockoutCountdown(seconds: Int) = viewModelScope.launch {
        var s = seconds
        while (s > 0) {
            _state.value = _state.value.copy(lockoutSeconds = s)
            delay(1000)
            s--
        }
        _state.value = _state.value.copy(lockoutSeconds = 0, error = null)
    }
}
