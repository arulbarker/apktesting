package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.vapestoreunik.madep.theme.BrandBlack
import com.vapestoreunik.madep.theme.BrandJet
import com.vapestoreunik.madep.theme.BrandSnow
import com.vapestoreunik.madep.theme.BrandYellow
import com.vapestoreunik.madep.theme.BrandYellowGlow

/**
 * Brand wordmark + monogram "K".
 *
 * Treatment: 100dp circle, BLACK fill with 3dp ELECTRIC YELLOW border ring.
 * Inside: huge "K" in yellow. Below: bold "KASIR" wordmark, then thin yellow
 * accent rule (32×2dp), then tracked "V A P E S T O R E" wordmark.
 * No gradient noise — clean, punchy, signage-like.
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
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(BrandJet, BrandBlack)))
                    .border(width = 3.dp, color = BrandYellow, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "K",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                    ),
                    color = BrandYellow,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
        Text(
            "KASIR",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
            ),
            color = BrandSnow,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        // YELLOW accent rule between wordmark lines — signature element
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(BrandYellowGlow),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "V A P E S T O R E",
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 8.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = BrandYellow,
            textAlign = TextAlign.Center,
        )
        if (showTagline) {
            Spacer(Modifier.height(14.dp))
            Text(
                "PREMIUM VAPE BOUTIQUE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
