package com.vapestoreunik.madep.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenStoreProfile: () -> Unit,
    onOpenChangePin: () -> Unit,
    onOpenCategoryManage: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenBackup: () -> Unit,
    onLock: () -> Unit,
    onBack: () -> Unit,
    vm: SettingsViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
            )
        },
    ) { p ->
        Column(Modifier.padding(p).verticalScroll(rememberScrollState())) {
            NavItem(
                "Profil Toko",
                state.storeName.ifBlank { "Belum diatur" },
                Icons.Default.Store,
                onOpenStoreProfile,
            )
            HorizontalDivider()
            Row(
                Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Percent, null)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("PPN / Pajak")
                    Text("${state.taxPercent}%", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = state.taxEnabled, onCheckedChange = vm::setTaxEnabled)
            }
            if (state.taxEnabled) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Persen pajak:")
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = state.taxPercent.toString(),
                        onValueChange = { v -> v.toIntOrNull()?.let(vm::setTaxPercent) },
                        suffix = { Text("%") },
                        modifier = Modifier.width(120.dp),
                    )
                }
            }
            HorizontalDivider()
            var footerText by remember(state.receiptFooter) { mutableStateOf(state.receiptFooter) }
            Column(Modifier.padding(16.dp)) {
                Text("Footer Struk", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = footerText,
                    onValueChange = { footerText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                )
                TextButton(onClick = { vm.setFooter(footerText) }) { Text("Simpan") }
            }
            HorizontalDivider()
            NavItem("Ubah PIN", null, Icons.Default.Lock, onOpenChangePin)
            HorizontalDivider()
            NavItem("Kelola Kategori", null, Icons.Default.Category, onOpenCategoryManage)
            HorizontalDivider()
            NavItem("Laporan", null, Icons.Default.Assessment, onOpenReports)
            HorizontalDivider()
            NavItem("Backup & Restore", null, Icons.Default.Backup, onOpenBackup)
            HorizontalDivider()
            NavItem("Kunci", "Kembali ke layar PIN", Icons.Default.Lock, onLock)
        }
    }
}

@Composable
private fun NavItem(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(icon, null) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
