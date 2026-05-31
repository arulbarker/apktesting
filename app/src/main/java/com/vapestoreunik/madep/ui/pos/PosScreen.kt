package com.vapestoreunik.madep.ui.pos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.common.RupiahFormatter
import com.vapestoreunik.madep.core.ui.components.CategoryChip
import com.vapestoreunik.madep.core.ui.components.EmptyState
import com.vapestoreunik.madep.core.ui.components.QtyStepper
import com.vapestoreunik.madep.domain.model.Cart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier,
    vm: PosViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showCart by remember { mutableStateOf(false) }

    Box(modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::setQuery,
                label = { Text("Cari") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryChip("Semua", state.selectedCategoryId == null) { vm.setCategory(null) }
                }
                items(state.categories.size) { i ->
                    val c = state.categories[i]
                    CategoryChip(c.name, state.selectedCategoryId == c.id) { vm.setCategory(c.id) }
                }
            }
            Spacer(Modifier.height(12.dp))
            if (state.products.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Inventory,
                    title = "Tidak ada produk",
                    subtitle = "Tambah produk di tab Produk dulu",
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.products, key = { it.id }) { p ->
                        Card(onClick = { vm.onProductTap(p) }) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    p.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 2,
                                )
                                Text(
                                    if (p.hasVariants) "Pilih varian" else "Tap untuk tambah",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.cart.itemCount > 0) {
            ExtendedFloatingActionButton(
                onClick = { showCart = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            ) {
                Icon(Icons.Default.ShoppingCart, null)
                Spacer(Modifier.width(8.dp))
                Text("${state.cart.itemCount} • ${RupiahFormatter.format(state.cart.rawSubtotal)}")
            }
        }

        state.variantPicker?.let { info ->
            ModalBottomSheet(onDismissRequest = vm::dismissPicker) {
                Column(Modifier.padding(16.dp)) {
                    Text(info.product.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    info.variants.forEach { v ->
                        ListItem(
                            headlineContent = { Text(v.name.ifBlank { "Default" }) },
                            supportingContent = {
                                Text("${RupiahFormatter.format(v.price)} • Stok ${v.stock}")
                            },
                            modifier = Modifier.clickable { vm.onVariantPicked(info.product, v) },
                        )
                    }
                }
            }
        }

        if (showCart) {
            ModalBottomSheet(onDismissRequest = { showCart = false }) {
                CartSheet(
                    cart = state.cart,
                    onQtyChange = vm::updateQty,
                    onRemove = vm::removeItem,
                    onCheckout = {
                        showCart = false
                        onCheckout()
                    },
                )
            }
        }
    }
}

@Composable
private fun CartSheet(
    cart: Cart,
    onQtyChange: (Long, Int) -> Unit,
    onRemove: (Long) -> Unit,
    onCheckout: () -> Unit,
) {
    Column(Modifier.padding(16.dp)) {
        Text("Keranjang", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        cart.items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("${item.productName} ${item.variantName}".trim())
                    Text(
                        "${RupiahFormatter.format(item.unitPrice)} × ${item.qty} = ${RupiahFormatter.format(item.subtotal)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                QtyStepper(item.qty, { onQtyChange(item.variantId, it) }, max = item.availableStock)
                IconButton(onClick = { onRemove(item.variantId) }) {
                    Icon(Icons.Default.Delete, "Hapus")
                }
            }
            HorizontalDivider()
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Subtotal: ${RupiahFormatter.format(cart.rawSubtotal)}",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onCheckout,
            modifier = Modifier.fillMaxWidth(),
            enabled = cart.items.isNotEmpty(),
        ) {
            Text("Bayar")
        }
    }
}
