package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MetricCardVariant { Primary, Surface, Tertiary }

/**
 * Card display untuk metric/angka penting:
 * - label uppercase letter-spaced
 * - big number displayMedium
 * - optional subtitle (small caption)
 *
 * Variant:
 * - Primary: slate-900 background (untuk omzet hero)
 * - Surface: default white (untuk metric biasa)
 * - Tertiary: amber container (untuk highlight)
 */
@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    variant: MetricCardVariant = MetricCardVariant.Surface,
    content: @Composable (() -> Unit)? = null,
) {
    val containerColor = when (variant) {
        MetricCardVariant.Primary -> MaterialTheme.colorScheme.primary
        MetricCardVariant.Surface -> MaterialTheme.colorScheme.surface
        MetricCardVariant.Tertiary -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = when (variant) {
        MetricCardVariant.Primary -> MaterialTheme.colorScheme.onPrimary
        MetricCardVariant.Surface -> MaterialTheme.colorScheme.onSurface
        MetricCardVariant.Tertiary -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    val labelColor: Color = when (variant) {
        MetricCardVariant.Primary -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        MetricCardVariant.Surface -> MaterialTheme.colorScheme.onSurfaceVariant
        MetricCardVariant.Tertiary -> MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (variant == MetricCardVariant.Primary) 4.dp else 1.dp,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = labelColor,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium,
            )
            subtitle?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor,
                )
            }
            content?.let {
                Spacer(Modifier.height(8.dp))
                it()
            }
        }
    }
}
