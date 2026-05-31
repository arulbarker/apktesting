package com.vapestoreunik.madep.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import com.vapestoreunik.madep.core.ui.components.PrimaryButton
import com.vapestoreunik.madep.core.ui.components.SecondaryButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    onBack: () -> Unit,
    vm: BackupRestoreViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream"),
    ) { uri -> uri?.let(vm::export) }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let(vm::import) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PrimaryButton(
                text = "Export Database",
                icon = Icons.Default.Backup,
                onClick = {
                    exportLauncher.launch("kasir-backup-${System.currentTimeMillis()}.db")
                },
                modifier = Modifier.fillMaxWidth(),
            )
            SecondaryButton(
                text = "Import Database",
                icon = Icons.Default.Restore,
                onClick = { importLauncher.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "Peringatan: Import akan MENGGANTI semua data saat ini. Backup dulu sebelum import.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
        state.message?.let {
            AlertDialog(
                onDismissRequest = vm::clearMessage,
                title = { Text(if (state.isError) "Gagal" else "Berhasil") },
                text = { Text(it) },
                confirmButton = { TextButton(onClick = vm::clearMessage) { Text("OK") } },
            )
        }
    }
}
