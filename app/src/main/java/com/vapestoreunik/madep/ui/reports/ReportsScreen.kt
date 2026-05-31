package com.vapestoreunik.madep.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.RupiahFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    vm: ReportsViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Harian") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
            )
        },
    ) { p ->
        LazyColumn(
            Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AssistChip(
                    onClick = { showDatePicker = true },
                    label = { Text(DateFormatter.formatDisplay(state.dateMillis)) },
                )
            }
            item {
                Card {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text("Ringkasan", style = MaterialTheme.typography.titleMedium)
                        Text(
                            RupiahFormatter.format(state.omzet),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            "${state.count} transaksi • ${state.itemsSold} item terjual",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            "Rata-rata per transaksi: ${RupiahFormatter.format(state.avgTicket)}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pembayaran", style = MaterialTheme.typography.titleSmall)
                        if (state.paymentBreakdown.isEmpty()) {
                            Text(
                                "Belum ada transaksi",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        } else {
                            state.paymentBreakdown.forEach { ps ->
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(ps.paymentMethod)
                                    Text(RupiahFormatter.format(ps.sum))
                                }
                            }
                        }
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Top 10 Produk", style = MaterialTheme.typography.titleSmall)
                        if (state.topProducts.isEmpty()) {
                            Text(
                                "Belum ada penjualan",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        } else {
                            state.topProducts.forEachIndexed { i, p ->
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

        if (showDatePicker) {
            val dpState = rememberDatePickerState(initialSelectedDateMillis = state.dateMillis)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        dpState.selectedDateMillis?.let { vm.setDate(it) }
                        showDatePicker = false
                    }) { Text("Pilih") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                },
            ) { DatePicker(state = dpState) }
        }
    }
}
