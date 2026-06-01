package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.theme.BrandJet
import com.vapestoreunik.madep.theme.BrandSnow
import com.vapestoreunik.madep.theme.BrandSteel
import com.vapestoreunik.madep.theme.BrandYellow
import com.vapestoreunik.madep.theme.PillShape

/**
 * Primary CTA — YELLOW PILL CAPSULE with black text.
 *
 * Treatment:
 * - Pill shape (RoundedCornerShape 50)
 * - Container: BrandYellow (electric)
 * - Content: BrandJet (near-black) — maximum contrast retail signage
 * - Height: 60dp (big touch target, easy on busy kasir)
 * - Label: UPPERCASE BOLD, tracking 1.5sp
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
        modifier = modifier.height(60.dp),
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandYellow,
            contentColor = BrandJet,
            disabledContainerColor = BrandYellow.copy(alpha = 0.30f),
            disabledContentColor = BrandJet.copy(alpha = 0.55f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = BrandJet,
                strokeWidth = 2.5.dp,
            )
        } else {
            icon?.let {
                Icon(it, contentDescription = null)
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                ),
            )
        }
    }
}

/**
 * Secondary — outlined pill capsule, yellow border, yellow text.
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
        modifier = modifier.height(52.dp),
        shape = PillShape,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) BrandYellow else BrandSteel,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = BrandYellow,
            disabledContentColor = BrandSnow.copy(alpha = 0.4f),
        ),
    ) {
        icon?.let {
            Icon(it, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp),
        )
    }
}
