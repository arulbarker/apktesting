package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.theme.KasirVapestoreTheme

@Composable
fun NumPad(
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val rows = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { d ->
                    DigitButton(d, enabled, Modifier.weight(1f)) { onDigit(d) }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(Modifier.weight(1f))
            DigitButton(0, enabled, Modifier.weight(1f)) { onDigit(0) }
            FilledTonalIconButton(
                onClick = onBackspace,
                enabled = enabled,
                modifier = Modifier.weight(1f).height(56.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Hapus")
            }
        }
    }
}

@Composable
private fun DigitButton(digit: Int, enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick, enabled = enabled, modifier = modifier.height(56.dp)) {
        Text("$digit", fontSize = 24.sp)
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun NumPadPreview() {
    KasirVapestoreTheme {
        NumPad(onDigit = {}, onBackspace = {}, modifier = Modifier.padding(16.dp))
    }
}
