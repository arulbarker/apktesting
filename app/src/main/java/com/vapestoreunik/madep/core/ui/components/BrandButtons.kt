package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Primary CTA button - amber gold, 56dp height, uppercase label.
 * Pakai untuk action paling penting di screen (Bayar, Selesaikan, Simpan).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f),
            disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onTertiary,
                strokeWidth = 2.dp,
            )
        } else {
            icon?.let {
                Icon(it, contentDescription = null)
                Spacer(Modifier.width(8.dp))
            }
            Text(text.uppercase(), style = MaterialTheme.typography.titleMedium)
        }
    }
}

/**
 * Secondary action - outlined, 48dp, slate-colored.
 * Pakai untuk action sekunder (Batal, Kembali ke form, dst).
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
    ) {
        icon?.let {
            Icon(it, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
