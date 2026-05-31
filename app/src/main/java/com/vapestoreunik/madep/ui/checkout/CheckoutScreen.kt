package com.vapestoreunik.madep.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.RupiahFormatter
import com.vapestoreunik.madep.core.ui.components.RupiahTextField
import com.vapestoreunik.madep.domain.model.DiscountInput
import com.vapestoreunik.madep.domain.model.DiscountType
import com.vapestoreunik.madep.domain.model.PaymentMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onSuccess: (Long) -> Unit,
    onBack: () -> Unit,
    vm: CheckoutViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.resultTransactionId) {
        state.resultTransactionId?.let { onSuccess(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
            )
        },
    ) { p ->
        Column(
            Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryCard(state)
            DiscountCard(state, vm)
            PaymentCard(state, vm)
            OutlinedTextField(
                value = state.note,
                onValueChange = vm::setNote,
                label = { Text("Catatan (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )

            state.error?.let {
                AlertDialog(
                    onDismissRequest = vm::clearError,
                    title = { Text("Gagal") },
                    text = { Text(it) },
                    confirmButton = { TextButton(onClick = vm::clearError) { Text("OK") } },
                )
            }

            val totalNow = state.totals?.total ?: 0L
            val canSubmit = state.totals != null && !state.processing &&
                state.cart.items.isNotEmpty() &&
                (state.method == PaymentMethod.NON_TUNAI || state.paid >= totalNow)

            Button(
                onClick = vm::submit,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
            ) {
                if (state.processing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                } else {
                    Text(
                        "SELESAIKAN TRANSAKSI",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(state: CheckoutState) {
    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            state.cart.items.forEach {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${it.productName} ${it.variantName}".trim() + "  ×${it.qty}")
                    Text(RupiahFormatter.format(it.subtotal))
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            state.totals?.let { t ->
                LineRow("Subtotal", RupiahFormatter.format(t.subtotal))
                if (t.discountAmount > 0) {
                    LineRow("Diskon", "-${RupiahFormatter.format(t.discountAmount)}")
                }
                if (t.taxAmount > 0) {
                    LineRow("Pajak ${state.taxPercent}%", RupiahFormatter.format(t.taxAmount))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL", style = MaterialTheme.typography.titleMedium)
                    Text(
                        RupiahFormatter.format(t.total),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun LineRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscountCard(state: CheckoutState, vm: CheckoutViewModel) {
    var discountType by remember(state.discount) { mutableStateOf<DiscountType?>(state.discount?.type) }
    var discountValue by remember(state.discount) { mutableStateOf(state.discount?.value ?: 0L) }

    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Diskon", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = discountType == null,
                    onClick = {
                        discountType = null
                        discountValue = 0L
                        vm.setDiscount(null)
                    },
                    label = { Text("Tidak") },
                )
                FilterChip(
                    selected = discountType == DiscountType.PERCENT,
                    onClick = { discountType = DiscountType.PERCENT },
                    label = { Text("Persen") },
                )
                FilterChip(
                    selected = discountType == DiscountType.NOMINAL,
                    onClick = { discountType = DiscountType.NOMINAL },
                    label = { Text("Nominal") },
                )
            }
            when (discountType) {
                DiscountType.PERCENT -> OutlinedTextField(
                    value = if (discountValue == 0L) "" else discountValue.toString(),
                    onValueChange = { v ->
                        val parsed = v.toLongOrNull()?.coerceIn(0, 100) ?: 0L
                        discountValue = parsed
                        vm.setDiscount(DiscountInput(DiscountType.PERCENT, parsed))
                    },
                    label = { Text("% (0-100)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                DiscountType.NOMINAL -> RupiahTextField(
                    value = discountValue,
                    onValueChange = { v ->
                        discountValue = v
                        vm.setDiscount(DiscountInput(DiscountType.NOMINAL, v))
                    },
                    label = "Nominal diskon",
                    modifier = Modifier.fillMaxWidth(),
                )
                else -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentCard(state: CheckoutState, vm: CheckoutViewModel) {
    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Metode Pembayaran", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.method == PaymentMethod.TUNAI,
                    onClick = { vm.setMethod(PaymentMethod.TUNAI) },
                    label = { Text("Tunai") },
                )
                FilterChip(
                    selected = state.method == PaymentMethod.NON_TUNAI,
                    onClick = { vm.setMethod(PaymentMethod.NON_TUNAI) },
                    label = { Text("Non-Tunai") },
                )
            }
            if (state.method == PaymentMethod.TUNAI) {
                RupiahTextField(
                    value = state.paid,
                    onValueChange = vm::setPaid,
                    label = "Jumlah dibayar",
                    modifier = Modifier.fillMaxWidth(),
                )
                state.totals?.let {
                    if (state.paid >= it.total && it.total > 0) {
                        Text(
                            "Kembalian: ${RupiahFormatter.format(it.change)}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}
