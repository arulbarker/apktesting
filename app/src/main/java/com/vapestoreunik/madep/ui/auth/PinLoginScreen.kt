package com.vapestoreunik.madep.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.ui.components.BrandHeader
import com.vapestoreunik.madep.core.ui.components.NumPad
import com.vapestoreunik.madep.core.ui.components.PrimaryButton
import com.vapestoreunik.madep.theme.BrandAsh
import com.vapestoreunik.madep.theme.BrandIron
import com.vapestoreunik.madep.theme.BrandJet
import com.vapestoreunik.madep.theme.BrandRed
import com.vapestoreunik.madep.theme.BrandSnow
import com.vapestoreunik.madep.theme.BrandYellow

@Composable
fun PinLoginScreen(
    onUnlock: () -> Unit,
    vm: PinLoginViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.unlocked) { if (state.unlocked) onUnlock() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandJet)
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BrandHeader(showTagline = false)
        Spacer(Modifier.height(40.dp))
        Text(
            "MASUKKAN PIN",
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = BrandAsh,
        )
        Spacer(Modifier.height(20.dp))
        // ── PIN dots — 20dp yellow filled, hollow when empty ────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            repeat(6) { i ->
                val filled = i < state.pin.length
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (filled) BrandYellow else BrandJet)
                        .border(
                            width = 2.dp,
                            color = if (filled) BrandYellow else BrandIron,
                            shape = CircleShape,
                        ),
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        state.error?.let {
            Text(it, color = BrandRed, style = MaterialTheme.typography.bodyMedium)
        }
        if (state.lockoutSeconds > 0) {
            Text(
                "TERKUNCI ${state.lockoutSeconds}s",
                color = BrandRed,
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
            )
        }
        Spacer(Modifier.height(36.dp))
        NumPad(
            onDigit = vm::appendDigit,
            onBackspace = vm::backspace,
            enabled = state.lockoutSeconds == 0,
            modifier = Modifier.widthIn(max = 320.dp),
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton(
            text = "Buka",
            onClick = vm::submit,
            enabled = state.pin.length in 4..6 && state.lockoutSeconds == 0,
            modifier = Modifier.widthIn(max = 320.dp).padding(horizontal = 16.dp),
        )
    }
}
