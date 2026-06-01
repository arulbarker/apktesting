package com.vapestoreunik.madep.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.RupiahFormatter
import com.vapestoreunik.madep.core.ui.components.MetricCard
import com.vapestoreunik.madep.core.ui.components.MetricCardVariant
import com.vapestoreunik.madep.theme.BrandGold400

@Composable
fun DashboardScreen(
    onOpenReports: () -> Unit,
    modifier: Modifier = Modifier,
    vm: DashboardViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                "Halo, ${state.storeName.ifBlank { "Vapestore Unik" }}!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                DateFormatter.formatDisplay(System.currentTimeMillis()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandGold400),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        "Lihat Laporan Detail",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.SemiBold,
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
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WarningAmber, null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Stok Rendah (${state.lowStock.size})",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        state.lowStock.take(5).forEach { v ->
                            Text("• ${v.name.ifBlank { "Default" }} — sisa ${v.stock}")
                        }
                    }
                }
            }
        }
        if (state.topProducts.isNotEmpty()) {
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Top 5 Hari Ini", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        state.topProducts.take(5).forEachIndexed { i, p ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("${i + 1}. ${p.productName} ${p.variantName}".trim())
                                Text("${p.qty}× • ${RupiahFormatter.format(p.omzet)}")
                            }
                        }
                    }
                }
            }
        }
    }
}
