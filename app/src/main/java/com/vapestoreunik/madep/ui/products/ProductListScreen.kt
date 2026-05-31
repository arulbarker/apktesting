package com.vapestoreunik.madep.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.ui.components.CategoryChip
import com.vapestoreunik.madep.core.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onOpenForm: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    vm: ProductListViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Box(modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::setQuery,
                label = { Text("Cari produk") },
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
                    title = "Belum ada produk",
                    subtitle = "Tap tombol + untuk menambah produk baru",
                    actionLabel = "Tambah Produk",
                    onAction = { onOpenForm(null) },
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(160.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.products, key = { it.id }) { p ->
                        Card(onClick = { onOpenForm(p.id) }) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    p.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 2,
                                )
                                p.brand?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { onOpenForm(null) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Icon(Icons.Default.Add, "Tambah")
        }
    }
}
