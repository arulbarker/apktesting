package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.theme.BrandCream50
import com.vapestoreunik.madep.theme.BrandGold500
import com.vapestoreunik.madep.theme.BrandGoldRoyal
import com.vapestoreunik.madep.theme.BrandOnyx900
import com.vapestoreunik.madep.theme.BrandOnyx950

enum class MetricCardVariant { Primary, Surface, Tertiary }

/**
 * Card untuk metric/angka penting.
 *
 * Variants:
 * - **Primary**: hero card — onyx→onyx950 vertical gradient, GOLD value,
 *   thin gold ring. Brand-locked (independent of light/dark theme) so the
 *   omzet display always looks premium retail.
 * - **Surface**: default white card untuk metric biasa.
 * - **Tertiary**: gold container untuk highlight.
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
            label = label,
            value = value,
            subtitle = subtitle,
            content = content,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            elevation = 1.dp,
        )
        MetricCardVariant.Tertiary -> StandardCard(
            label = label,
            value = value,
            subtitle = subtitle,
            content = content,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
            elevation = 1.dp,
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
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = BrandCream50,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BrandOnyx900, BrandOnyx950),
                    ),
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Tiny gold accent bar — visual brand signature
            Spacer(
                Modifier
                    .height(3.dp)
                    .width(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandGoldRoyal),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = BrandCream50.copy(alpha = 0.65f),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                ),
                color = BrandGold500,
            )
            subtitle?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandCream50.copy(alpha = 0.75f),
                )
            }
            content?.let {
                Spacer(Modifier.height(10.dp))
                it()
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
    contentColor: Color,
    labelColor: Color,
    elevation: androidx.compose.ui.unit.Dp,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
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

