package com.vapestoreunik.madep.ui.transactions

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.repository.TransactionRepository
import com.vapestoreunik.madep.domain.model.ReceiptData
import com.vapestoreunik.madep.domain.model.ReceiptLine
import com.vapestoreunik.madep.domain.usecase.BuildReceiptPdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ReceiptState(
    val data: ReceiptData? = null,
    val loading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val txRepo: TransactionRepository,
    private val prefs: AppPreferences,
    private val pdfUseCase: BuildReceiptPdfUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ReceiptState())
    val state = _state.asStateFlow()

    fun load(transactionId: Long) = viewModelScope.launch {
        val tx = txRepo.getById(transactionId) ?: run {
            _state.value = ReceiptState(loading = false, error = "Transaksi tidak ditemukan")
            return@launch
        }
        val items = txRepo.itemsOf(transactionId)
        val profile = prefs.storeProfile.first()
        val footer = prefs.receiptFooter.first()
        val data = ReceiptData(
            storeName = profile.name,
            storeAddress = profile.address,
            storePhone = profile.phone,
            transactionCode = tx.code,
            createdAtMillis = tx.createdAt,
            lines = items.map {
                ReceiptLine(
                    productName = it.productNameSnapshot,
                    variantName = it.variantNameSnapshot,
                    qty = it.qty,
                    unitPrice = it.priceSnapshot,
                    subtotal = it.subtotal,
                )
            },
            subtotal = tx.subtotal,
            discountAmount = tx.discountAmount,
            taxAmount = tx.taxAmount,
            total = tx.total,
            paymentMethod = tx.paymentMethod,
            paid = tx.paid,
            change = tx.change,
            footer = footer,
        )
        _state.value = ReceiptState(data = data, loading = false)
    }

    fun share(onShare: (Intent) -> Unit) = viewModelScope.launch {
        val data = _state.value.data ?: return@launch
        pdfUseCase.execute(data)
            .onSuccess { file ->
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                onShare(Intent.createChooser(intent, "Bagikan struk"))
            }
            .onFailure { _state.value = _state.value.copy(error = it.message) }
    }
}
