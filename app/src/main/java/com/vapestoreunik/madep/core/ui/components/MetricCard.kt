package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.vapestoreunik.madep.theme.BrandAsh
import com.vapestoreunik.madep.theme.BrandCarbon
import com.vapestoreunik.madep.theme.BrandJet
import com.vapestoreunik.madep.theme.BrandSnow
import com.vapestoreunik.madep.theme.BrandYellow
import com.vapestoreunik.madep.theme.BrandYellowGlow

enum class MetricCardVariant { Primary, Surface, Tertiary }

/**
 * MetricCard — display for important numbers.
 *
 * Variants:
 * - **Primary** (hero — omzet, period total): 5dp ELECTRIC YELLOW left edge
 *   stripe, black surface, value at 44sp in YELLOW. Thick visual weight.
 * - **Surface**: default carbon card, white value.
 * - **Tertiary**: yellow-tinted container highlight.
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
    when (variant) {
        MetricCardVariant.Primary -> PrimaryHeroCard(label, value, subtitle, content, modifier)
        MetricCardVariant.Surface -> StandardCard(
            label, value, subtitle, content, modifier,
            containerColor = MaterialTheme.colorScheme.surface,
            valueColor = BrandSnow,
            labelColor = BrandAsh,
        )
        MetricCardVariant.Tertiary -> StandardCard(
            label, value, subtitle, content, modifier,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            valueColor = BrandJet,
            labelColor = BrandJet.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun PrimaryHeroCard(
    label: String,
    value: String,
    subtitle: String?,
    content: @Composable (() -> Unit)?,
    modifier: Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrandJet, contentColor = BrandSnow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BrandCarbon),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // 5dp left ELECTRIC YELLOW edge stripe — full height
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(BrandYellowGlow),
            )
            Column(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 22.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = BrandAsh,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                    ),
                    color = BrandYellow,
                )
                subtitle?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandSnow.copy(alpha = 0.75f),
                    )
                }
                content?.let {
                    Spacer(Modifier.height(12.dp))
                    it()
                }
            }
        }
    }
}

@Composable
private fun StandardCard(
    label: String,
    value: String,
    subtitle: String?,
    content: @Composable (() -> Unit)?,
    modifier: Modifier,
    containerColor: Color,
    valueColor: Color,
    labelColor: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = BrandSnow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = labelColor,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                color = valueColor,
            )
            subtitle?.let {
                Spacer(Modifier.height(2.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = labelColor)
            }
            content?.let {
                Spacer(Modifier.height(8.dp))
                it()
            }
        }
    }
}

