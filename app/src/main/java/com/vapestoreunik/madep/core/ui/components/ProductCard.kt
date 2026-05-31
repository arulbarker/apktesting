package com.vapestoreunik.madep.core.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.core.common.RupiahFormatter

/**
 * Card produk dengan:
 * - placeholder image (gradient + icon) — siap kalau imageUri ditambah di Tier B
 * - nama produk (titleSmall, max 2 lines)
 * - brand kecil (bodySmall, onSurfaceVariant)
 * - harga prominent (titleMedium, color tertiary/amber kalau ada)
 * - badge stok rendah (kalau low)
 * - hint "Tap untuk tambah" / "Pilih varian" kalau dipakai di POS
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.LocalMall,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                )
                if (outOfStock) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "HABIS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = MaterialTheme.colorScheme.onError,
                        )
                    }
                } else if (lowStock) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopStart),
                    ) {
                        Text(
                            "STOK RENDAH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                brand?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    priceLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                hint?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
