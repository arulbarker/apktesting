package com.vapestoreunik.madep.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.local.KasirDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BackupState(val message: String? = null, val isError: Boolean = false)

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: KasirDatabase,
) : ViewModel() {
    private val _state = MutableStateFlow(BackupState())
    val state = _state.asStateFlow()

    fun export(target: Uri) = viewModelScope.launch {
        runCatching {
            val dbFile = context.getDatabasePath(KasirDatabase.DB_NAME)
            context.contentResolver.openOutputStream(target)?.use { out ->
                dbFile.inputStream().use { it.copyTo(out) }
            } ?: throw KasirException.StorageWriteFailed(
                IllegalStateException("Tidak bisa membuka file tujuan"),
            )
        }.onSuccess { _state.value = BackupState("Backup berhasil") }
            .onFailure { _state.value = BackupState(it.message, true) }
    }

    fun import(source: Uri) = viewModelScope.launch {
        runCatching {
            val tmp = File(context.cacheDir, "import.db")
            context.contentResolver.openInputStream(source)?.use { input ->
                tmp.outputStream().use { input.copyTo(it) }
            } ?: throw KasirException.BackupFileInvalid()
            // Validasi SQLite header
            tmp.inputStream().use { input ->
                val header = ByteArray(16)
                input.read(header)
                if (String(header).take(15) != "SQLite format 3") {
                    throw KasirException.BackupFileInvalid()
                }
            }
            db.close()
            val target = context.getDatabasePath(KasirDatabase.DB_NAME)
            tmp.copyTo(target, overwrite = true)
            tmp.delete()
        }.onSuccess { _state.value = BackupState("Restore berhasil — restart aplikasi", false) }
            .onFailure { _state.value = BackupState(it.message, true) }
    }

    fun clearMessage() { _state.value = BackupState() }
}
