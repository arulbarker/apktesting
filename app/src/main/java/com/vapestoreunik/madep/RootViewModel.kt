package com.vapestoreunik.madep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class RootState(val setupCompleted: Boolean = false)

@HiltViewModel
class RootViewModel @Inject constructor(prefs: AppPreferences) : ViewModel() {
    val state = prefs.setupCompleted
        .map { RootState(setupCompleted = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, RootState())
}
