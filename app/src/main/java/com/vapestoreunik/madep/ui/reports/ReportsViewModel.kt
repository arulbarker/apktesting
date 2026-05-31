package com.vapestoreunik.madep.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.data.local.dao.PaymentSum
import com.vapestoreunik.madep.data.local.dao.TopProductRow
import com.vapestoreunik.madep.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportsState(
    val dateMillis: Long = System.currentTimeMillis(),
    val omzet: Long = 0L,
    val count: Int = 0,
    val itemsSold: Int = 0,
    val avgTicket: Long = 0L,
    val paymentBreakdown: List<PaymentSum> = emptyList(),
    val topProducts: List<TopProductRow> = emptyList(),
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val txRepo: TransactionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ReportsState())
    val state = _state.asStateFlow()

    init { setDate(System.currentTimeMillis()) }

    fun setDate(millis: Long) = viewModelScope.launch {
        val from = DateFormatter.startOfDayMillis(millis)
        val to = DateFormatter.endOfDayMillis(millis)
        val omzet = txRepo.omzetInRange(from, to)
        val count = txRepo.countInRange(from, to)
        val avg = if (count > 0) omzet / count else 0L
        val payments = txRepo.omzetByMethod(from, to)
        val tops = txRepo.topProducts(from, to, limit = 10)
        val itemsSold = tops.sumOf { it.qty }
        _state.value = ReportsState(millis, omzet, count, itemsSold, avg, payments, tops)
    }
}
