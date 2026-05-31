package com.vapestoreunik.madep.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.ui.components.QtyStepper
import com.vapestoreunik.madep.core.ui.components.RupiahTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    vm: ProductFormViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(productId) { vm.load(productId) }
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "Produk Baru" else "Ubah Produk") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = { TextButton(onClick = vm::save) { Text("Simpan") } },
            )
        },
    ) { p ->
        LazyColumn(
            modifier = Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { BasicInfoSection(state, vm) }
            itemsIndexed(state.variants) { i, v ->
                VariantCard(
                    draft = v,
                    showVariantName = state.hasVariants,
                    canRemove = state.variants.size > 1 && state.hasVariants,
                    onUpdate = { vm.updateVariant(i, it) },
                    onRemove = { vm.removeVariant(i) },
                )
            }
            if (state.hasVariants) {
                item {
                    OutlinedButton(onClick = vm::addVariant, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Tambah Varian")
                    }
                }
            }
            state.error?.let {
                item { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicInfoSection(state: ProductFormState, vm: ProductFormViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = state.name,
            onValueChange = vm::setName,
            label = { Text("Nama Produk *") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.brand,
            onValueChange = vm::setBrand,
            label = { Text("Brand") },
            modifier = Modifier.fillMaxWidth(),
        )
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = state.categories.find { it.id == state.categoryId }?.name ?: "Pilih kategori",
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategori *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                state.categories.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.name) },
                        onClick = {
                            vm.setCategory(c.id)
                            expanded = false
                        },
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Punya varian (rasa/ukuran/nikotin)", modifier = Modifier.weight(1f))
            Switch(checked = state.hasVariants, onCheckedChange = vm::toggleHasVariants)
        }
    }
}

@Composable
private fun VariantCard(
    draft: VariantDraft,
    showVariantName: Boolean,
    canRemove: Boolean,
    onUpdate: ((VariantDraft) -> VariantDraft) -> Unit,
    onRemove: () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (showVariantName) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = draft.name,
                        onValueChange = { v -> onUpdate { it.copy(name = v) } },
                        label = { Text("Nama Varian (cth: 30ml 3mg)") },
                        modifier = Modifier.weight(1f),
                    )
                    if (canRemove) {
                        IconButton(onClick = onRemove) { Icon(Icons.Default.Delete, "Hapus") }
                    }
                }
            }
            OutlinedTextField(
                value = draft.sku,
                onValueChange = { v -> onUpdate { it.copy(sku = v) } },
                label = { Text("SKU (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = draft.barcode,
                onValueChange = { v -> onUpdate { it.copy(barcode = v) } },
                label = { Text("Barcode (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            RupiahTextField(
                value = draft.price,
                onValueChange = { p -> onUpdate { it.copy(price = p) } },
                label = "Harga",
                modifier = Modifier.fillMaxWidth(),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Stok awal:")
                Spacer(Modifier.width(8.dp))
                QtyStepper(draft.stock, { s -> onUpdate { it.copy(stock = s) } })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Threshold low-stock:")
                Spacer(Modifier.width(8.dp))
                QtyStepper(
                    value = draft.lowStockThreshold,
                    onChange = { t -> onUpdate { it.copy(lowStockThreshold = t) } },
                    min = 0,
                )
            }
        }
    }
}
