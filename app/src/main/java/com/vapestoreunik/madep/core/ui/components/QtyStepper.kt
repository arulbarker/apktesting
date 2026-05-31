package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun QtyStepper(
    value: Int,
    onChange: (Int) -> Unit,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        FilledTonalIconButton(
            onClick = { onChange((value - 1).coerceAtLeast(min)) },
            enabled = value > min,
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Kurangi")
        }
        Text(
            "$value",
            modifier = Modifier.widthIn(min = 32.dp).padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        FilledTonalIconButton(
            onClick = { onChange((value + 1).coerceAtMost(max)) },
            enabled = value < max,
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah")
        }
    }
}

@Preview
@Composable
private fun QtyStepperPreview() {
    var v by remember { mutableStateOf(2) }
    QtyStepper(value = v, onChange = { v = it }, modifier = Modifier.padding(16.dp))
}
