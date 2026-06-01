package com.vapestoreunik.madep.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.RupiahFormatter
import com.vapestoreunik.madep.core.ui.components.MetricCard
import com.vapestoreunik.madep.core.ui.components.MetricCardVariant
import com.vapestoreunik.madep.core.ui.components.SectionHeader
import com.vapestoreunik.madep.theme.BrandAsh
import com.vapestoreunik.madep.theme.BrandCarbon
import com.vapestoreunik.madep.theme.BrandIron
import com.vapestoreunik.madep.theme.BrandJet
import com.vapestoreunik.madep.theme.BrandRed
import com.vapestoreunik.madep.theme.BrandSnow
import com.vapestoreunik.madep.theme.BrandYellow

@Composable
fun DashboardScreen(
    onOpenReports: () -> Unit,
    modifier: Modifier = Modifier,
    vm: DashboardViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
    ) {
        // ── Greeting hero ───────────────────────────────────────────────────
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(BrandYellow),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        DateFormatter.formatDisplay(System.currentTimeMillis()).uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                        color = BrandAsh,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Halo, ${state.storeName.ifBlank { "Vapestore" }}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = BrandSnow,
                )
            }
        }
        // ── HERO omzet metric ───────────────────────────────────────────────
        item {
            MetricCard(
                label = "Omzet Hari Ini",
                value = RupiahFormatter.format(state.today.omzet),
                subtitle = "${state.today.transactionCount} transaksi • Rata-rata ${RupiahFormatter.format(state.today.averageTicket)}",
                variant = MetricCardVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = onOpenReports,
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandYellow),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        "LIHAT LAPORAN DETAIL",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Black,
                        ),
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
        if (state.lowStock.isNotEmpty()) {
            item { SectionHeader(title = "Stok Rendah") }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandCarbon),
                    border = BorderStroke(1.dp, BrandRed.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = BrandRed,
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "${state.lowStock.size} ITEM PERLU RESTOCK",
                                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.5.sp),
                                color = BrandRed,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        state.lowStock.take(5).forEach { v ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    "• ${v.name.ifBlank { "Default" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrandSnow,
                                )
                                Text(
                                    "${v.stock} pcs",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = BrandRed,
                                )
                            }
                        }
                    }
                }
            }
        }
        if (state.topProducts.isNotEmpty()) {
            item { SectionHeader(title = "Top 5 Hari Ini") }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandCarbon),
                    border = BorderStroke(1.dp, BrandIron),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(Modifier.padding(18.dp)) {
                        state.topProducts.take(5).forEachIndexed { i, p ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(BrandYellow),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            "${i + 1}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                            ),
                                            color = BrandJet,
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            p.productName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            color = BrandSnow,
                                        )
                                        if (p.variantName.isNotBlank()) {
                                            Text(
                                                p.variantName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = BrandAsh,
                                            )
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "${p.qty}× terjual",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandAsh,
                                    )
                                    Text(
                                        RupiahFormatter.format(p.omzet),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = BrandYellow,
                                    )
                                }
                            }
                            if (i < state.topProducts.take(5).size - 1) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(BrandIron),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
