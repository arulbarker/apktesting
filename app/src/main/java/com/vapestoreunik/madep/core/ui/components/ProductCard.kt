package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.theme.BrandAsh
import com.vapestoreunik.madep.theme.BrandCarbon
import com.vapestoreunik.madep.theme.BrandIron
import com.vapestoreunik.madep.theme.BrandJet
import com.vapestoreunik.madep.theme.BrandRed
import com.vapestoreunik.madep.theme.BrandSnow
import com.vapestoreunik.madep.theme.BrandYellow

/**
 * ProductCard — black thumbnail + YELLOW PRICE CHIP overlay.
 *
 * Treatment:
 * - Dark thumbnail (BrandIron) with a faint yellow corner triangle accent
 *   — like a streetwear hangtag
 * - YELLOW price chip (rounded badge, yellow bg + black text) instead of plain text
 * - Bold name, asher brand line
 * - Out-of-stock: thumbnail dimmed + red strip overlay
 */
@Composable
fun ProductCard(
    name: String,
    brand: String?,
    priceLabel: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    lowStock: Boolean = false,
    outOfStock: Boolean = false,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = !outOfStock,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandCarbon, contentColor = BrandSnow),
        border = BorderStroke(1.dp, BrandIron),
    ) {
        Column {
            // ── Thumbnail: dark slab with a small yellow corner ─────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .background(BrandIron),
                contentAlignment = Alignment.Center,
            ) {
                // Yellow corner triangle accent (top-right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(width = 28.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BrandYellow),
                )
                Icon(
                    Icons.Default.LocalMall,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = BrandAsh.copy(alpha = 0.45f),
                )
                if (outOfStock) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(BrandRed.copy(alpha = 0.92f))
                            .align(Alignment.BottomCenter),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "H A B I S",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 4.sp,
                                fontWeight = FontWeight.Black,
                            ),
                            color = BrandSnow,
                        )
                    }
                } else if (lowStock) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .background(BrandRed.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .align(Alignment.TopStart),
                    ) {
                        Text(
                            "STOK RENDAH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = BrandRed,
                        )
                    }
                }
            }
            // ── Text block ──────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    color = BrandSnow,
                )
                brand?.let {
                    Text(
                        it.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                        color = BrandAsh,
                    )
                }
                Spacer(Modifier.height(8.dp))
                // ── YELLOW PRICE CHIP — the punchline of the card ───────────
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandYellow)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        priceLabel,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.2.sp,
                        ),
                        color = BrandJet,
                    )
                }
                hint?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandAsh,
                    )
                }
            }
        }
    }
}
