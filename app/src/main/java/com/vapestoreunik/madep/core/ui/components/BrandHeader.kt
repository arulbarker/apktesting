package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.theme.BrandGold500
import com.vapestoreunik.madep.theme.BrandGoldRoyal
import com.vapestoreunik.madep.theme.BrandOnyx900
import com.vapestoreunik.madep.theme.BrandOnyx950

/**
 * Brand wordmark + monogram "K".
 *
 * Visual signature: ONYX → GOLD diagonal gradient on the monogram circle,
 * thin gold ring outline. Cream "K" stamped in the center. The wordmark
 * uses onyx for "KASIR" and gold-royal for "VAPESTORE" tracked letterspacing.
 */
@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    showMonogram: Boolean = true,
    showTagline: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showMonogram) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BrandOnyx950, BrandOnyx900),
                        ),
                    )
                    .border(width = 1.5.dp, color = BrandGoldRoyal, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "K",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                    ),
                    color = BrandGold500,
                )
            }
            Spacer(Modifier.height(20.dp))
        }
        Text(
            "KASIR",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "V A P E S T O R E",
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 6.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = BrandGoldRoyal,
            textAlign = TextAlign.Center,
        )
        if (showTagline) {
            Spacer(Modifier.height(10.dp))
            Text(
                "PREMIUM VAPE BOUTIQUE · POS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
