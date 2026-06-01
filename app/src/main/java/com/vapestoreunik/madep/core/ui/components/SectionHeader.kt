package com.vapestoreunik.madep.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.theme.BrandYellow

/**
 * Section header — UPPERCASE label with a thick yellow vertical bar on the
 * left as visual anchor. Optional trailing TextButton action.
 *
 * [▌] PRODUK UNGGULAN              Lihat semua →
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 4dp×18dp yellow bar — visual anchor for the section
            Spacer(
                Modifier
                    .size(width = 4.dp, height = 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandYellow),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.5.sp),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    action,
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandYellow,
                )
            }
        }
    }
}

