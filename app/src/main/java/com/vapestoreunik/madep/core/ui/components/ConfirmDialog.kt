package com.vapestoreunik.madep.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String = "Konfirmasi",
    dismissLabel: String = "Batal",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            if (isDestructive) {
                TextButton(onClick = onConfirm) {
                    Text(confirmLabel, color = MaterialTheme.colorScheme.error)
                }
            } else {
                TextButton(onClick = onConfirm) { Text(confirmLabel) }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissLabel) } },
    )
}
