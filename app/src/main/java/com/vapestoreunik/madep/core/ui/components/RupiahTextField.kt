package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.vapestoreunik.madep.core.common.RupiahFormatter

@Composable
fun RupiahTextField(
    value: Long,
    onValueChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var text by remember(value) {
        mutableStateOf(formatForField(value))
    }
    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            val parsed = RupiahFormatter.parse(input)
            text = formatForField(parsed)
            onValueChange(parsed)
        },
        label = { Text(label) },
        prefix = { Text("Rp ") },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}

private fun formatForField(value: Long): String =
    if (value == 0L) "" else RupiahFormatter.format(value).removePrefix("Rp ").removePrefix("-Rp ")
