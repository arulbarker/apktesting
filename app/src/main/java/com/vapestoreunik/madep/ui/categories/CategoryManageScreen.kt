package com.vapestoreunik.madep.ui.categories

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.data.local.entity.CategoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManageScreen(
    onBack: () -> Unit,
    vm: CategoryManageViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kategori") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
            ) {
                Icon(Icons.Default.Add, "Tambah")
            }
        },
    ) { p ->
        LazyColumn(Modifier.padding(p).fillMaxSize()) {
            items(state.categories, key = { it.id }) { cat ->
                ListItem(
                    headlineContent = { Text(cat.name) },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { editTarget = cat }) {
                                Icon(Icons.Default.Edit, "Ubah")
                            }
                            IconButton(onClick = { vm.delete(cat) }) {
                                Icon(Icons.Default.Delete, "Hapus")
                            }
                        }
                    },
                )
                HorizontalDivider()
            }
        }
        error?.let {
            AlertDialog(
                onDismissRequest = vm::clearError,
                title = { Text("Tidak bisa dihapus") },
                text = { Text(it) },
                confirmButton = { TextButton(onClick = vm::clearError) { Text("OK") } },
            )
        }
        if (showAdd) {
            NameDialog("Tambah Kategori", "") { name ->
                showAdd = false
                if (name != null) vm.add(name)
            }
        }
        editTarget?.let { c ->
            NameDialog("Ubah Kategori", c.name) { name ->
                if (name != null) vm.rename(c, name)
                editTarget = null
            }
        }
    }
}

@Composable
private fun NameDialog(title: String, initial: String, onResult: (String?) -> Unit) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = { onResult(null) },
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                singleLine = true,
            )
        },
        confirmButton = { TextButton(onClick = { onResult(name) }) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = { onResult(null) }) { Text("Batal") } },
    )
}
