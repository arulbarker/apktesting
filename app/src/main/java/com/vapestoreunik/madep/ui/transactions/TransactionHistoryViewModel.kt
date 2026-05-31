package com.vapestoreunik.madep.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class HistoryState(
    val transactions: List<TransactionEntity> = emptyList(),
    val from: Long = DateFormatter.startOfDayMillis(System.currentTimeMillis()),
    val to: Long = DateFormatter.endOfDayMillis(System.currentTimeMillis()),
    val method: String? = null,
    val omzet: Long = 0L,
    val count: Int = 0,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val txRepo: TransactionRepository,
) : ViewModel() {
    private val _from = MutableStateFlow(DateFormatter.startOfDayMillis(System.currentTimeMillis()))
    private val _to = MutableStateFlow(DateFormatter.endOfDayMillis(System.currentTimeMillis()))
    private val _method = MutableStateFlow<String?>(null)

    private val txFlow = combine(_from, _to, _method) { f, t, m -> Triple(f, t, m) }
        .flatMapLatest { (f, t, m) -> txRepo.observeRange(f, t, m) }

    val state: StateFlow<HistoryState> = combine(txFlow, _from, _to, _method) { txs, f, t, m ->
        HistoryState(txs, f, t, m, omzet = txs.sumOf { it.total }, count = txs.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryState())

    fun setDate(from: Long, to: Long) { _from.value = from; _to.value = to }
    fun setMethod(m: String?) { _method.value = m }
}
