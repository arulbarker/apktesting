package com.vapestoreunik.madep.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.ui.components.NumPad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePinScreen(
    onDone: () -> Unit,
    onBack: () -> Unit,
    vm: ChangePinViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.done) { if (state.done) onDone() }

    val title = when (state.step) {
        0 -> "Masukkan PIN Lama"
        1 -> "Masukkan PIN Baru (4-6 digit)"
        else -> "Konfirmasi PIN Baru"
    }
    val currentPin = when (state.step) {
        0 -> state.oldPin
        1 -> state.newPin
        else -> state.confirmPin
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubah PIN") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, null) }
                },
            )
        },
    ) { p ->
        Column(
            Modifier.padding(p).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text("•".repeat(currentPin.length), style = MaterialTheme.typography.headlineMedium)
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            NumPad(
                onDigit = vm::appendDigit,
                onBackspace = vm::backspace,
                modifier = Modifier.widthIn(max = 320.dp),
            )
        }
    }
}
