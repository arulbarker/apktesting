package com.vapestoreunik.madep.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.RupiahFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    transactionId: Long,
    onNewTransaction: () -> Unit,
    onBack: () -> Unit,
    vm: ReceiptViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(transactionId) { vm.load(transactionId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Struk") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { vm.share { intent -> context.startActivity(intent) } }) {
                        Icon(Icons.Default.Share, "Bagikan")
                    }
                },
            )
        },
    ) { p ->
        when {
            state.loading -> CircularProgressIndicator(modifier = Modifier.padding(p))
            state.error != null -> Text(
                state.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(p).padding(16.dp),
            )
            state.data != null -> ReceiptBody(
                state.data!!,
                onNewTransaction = onNewTransaction,
                modifier = Modifier.padding(p).padding(16.dp),
            )
        }
    }
}

@Composable
private fun ReceiptBody(
    d: com.vapestoreunik.madep.domain.model.ReceiptData,
    onNewTransaction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(d.storeName, style = MaterialTheme.typography.titleLarge)
        if (d.storeAddress.isNotBlank()) Text(d.storeAddress)
        if (d.storePhone.isNotBlank()) Text(d.storePhone)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        Text(
            "${d.transactionCode}   ${DateFormatter.formatDisplay(d.createdAtMillis)} ${DateFormatter.formatTime(d.createdAtMillis)}",
            style = MaterialTheme.typography.bodySmall,
        )
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        d.lines.forEach { l ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${l.productName} ${l.variantName}".trim() + "  ×${l.qty}")
                Text(RupiahFormatter.format(l.subtotal))
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        Line("Subtotal", RupiahFormatter.format(d.subtotal))
        if (d.discountAmount > 0) Line("Diskon", "-${RupiahFormatter.format(d.discountAmount)}")
        if (d.taxAmount > 0) Line("Pajak", RupiahFormatter.format(d.taxAmount))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TOTAL", style = MaterialTheme.typography.titleMedium)
            Text(RupiahFormatter.format(d.total), style = MaterialTheme.typography.titleMedium)
        }
        Line("Bayar (${d.paymentMethod})", RupiahFormatter.format(d.paid))
        if (d.change > 0) Line("Kembalian", RupiahFormatter.format(d.change))
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        d.footer.lines().forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNewTransaction, modifier = Modifier.fillMaxWidth()) {
            Text("Transaksi Baru")
        }
    }
}

@Composable
private fun Line(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value)
    }
}
