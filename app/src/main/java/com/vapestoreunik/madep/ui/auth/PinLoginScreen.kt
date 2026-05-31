package com.vapestoreunik.madep.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.ui.components.BrandHeader
import com.vapestoreunik.madep.core.ui.components.NumPad

@Composable
fun PinLoginScreen(
    onUnlock: () -> Unit,
    vm: PinLoginViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.unlocked) { if (state.unlocked) onUnlock() }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BrandHeader(showTagline = false)
        Spacer(Modifier.height(32.dp))
        Text(
            "Masukkan PIN",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(6) { i ->
                val filled = i < state.pin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                        ),
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (state.lockoutSeconds > 0) {
            Text(
                "Terkunci ${state.lockoutSeconds} detik",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(32.dp))
        NumPad(
            onDigit = vm::appendDigit,
            onBackspace = vm::backspace,
            enabled = state.lockoutSeconds == 0,
            modifier = Modifier.widthIn(max = 320.dp),
        )
        Spacer(Modifier.height(16.dp))
        // OK button: aktif kalau pin >= 4 digit dan tidak lockout
        Button(
            onClick = vm::submit,
            enabled = state.pin.length in 4..6 && state.lockoutSeconds == 0,
            modifier = Modifier.widthIn(max = 320.dp).padding(horizontal = 16.dp),
        ) {
            Text("OK")
        }
    }
}
