package com.vapestoreunik.madep.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.RupiahFormatter

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
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                DateFormatter.formatDisplay(System.currentTimeMillis()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "OMZET HARI INI",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    )
                    Text(
                        RupiahFormatter.format(state.today.omzet),
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${state.today.transactionCount} transaksi • Rata-rata ${RupiahFormatter.format(state.today.averageTicket)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = onOpenReports,
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        ),
                    ) { Text("Lihat Laporan Detail →") }
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
