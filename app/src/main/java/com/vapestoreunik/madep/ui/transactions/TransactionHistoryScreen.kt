package com.vapestoreunik.madep.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.RupiahFormatter
import com.vapestoreunik.madep.core.ui.components.CategoryChip
import com.vapestoreunik.madep.core.ui.components.EmptyState
import com.vapestoreunik.madep.core.ui.components.MetricCard
import com.vapestoreunik.madep.core.ui.components.MetricCardVariant
import com.vapestoreunik.madep.core.ui.components.SectionHeader

@Composable
fun TransactionHistoryScreen(
    onOpenReceipt: (Long) -> Unit,
    modifier: Modifier = Modifier,
    vm: TransactionHistoryViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(modifier.fillMaxSize().padding(16.dp)) {
        MetricCard(
            label = "Periode ${DateFormatter.formatDisplay(state.from)} - ${DateFormatter.formatDisplay(state.to)}",
            value = RupiahFormatter.format(state.omzet),
            subtitle = "${state.count} transaksi",
            variant = MetricCardVariant.Primary,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        SectionHeader(title = "Filter")
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { CategoryChip("Semua", state.method == null) { vm.setMethod(null) } }
            item { CategoryChip("Tunai", state.method == "TUNAI") { vm.setMethod("TUNAI") } }
            item { CategoryChip("Non-Tunai", state.method == "NON_TUNAI") { vm.setMethod("NON_TUNAI") } }
        }
        Spacer(Modifier.height(12.dp))
        if (state.transactions.isEmpty()) {
            EmptyState(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                title = "Belum ada transaksi",
                subtitle = "Transaksi yang dibuat akan muncul di sini",
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(state.transactions, key = { it.id }) { tx ->
                    ListItem(
                        headlineContent = { Text(tx.code) },
                        supportingContent = {
                            Text("${DateFormatter.formatTime(tx.createdAt)} • ${tx.paymentMethod}")
                        },
                        trailingContent = { Text(RupiahFormatter.format(tx.total)) },
                        modifier = Modifier.clickable { onOpenReceipt(tx.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
