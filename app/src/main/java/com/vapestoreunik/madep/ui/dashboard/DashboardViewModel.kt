package com.vapestoreunik.madep.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.data.local.dao.TopProductRow
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import com.vapestoreunik.madep.data.preferences.AppPreferences
import com.vapestoreunik.madep.data.repository.ProductRepository
import com.vapestoreunik.madep.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TodaySummary(
    val omzet: Long = 0,
    val transactionCount: Int = 0,
    val averageTicket: Long = 0,
)

data class DashboardState(
    val storeName: String = "",
    val today: TodaySummary = TodaySummary(),
    val lowStock: List<VariantEntity> = emptyList(),
    val topProducts: List<TopProductRow> = emptyList(),
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val txRepo: TransactionRepository,
    productRepo: ProductRepository,
    prefs: AppPreferences,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.storeProfile.collect { p ->
                _state.value = _state.value.copy(storeName = p.name)
            }
        }
        viewModelScope.launch {
            productRepo.observeLowStock(5).collect { list ->
                _state.value = _state.value.copy(lowStock = list)
            }
        }
        refreshToday()
    }

    fun refreshToday() = viewModelScope.launch {
        val now = System.currentTimeMillis()
        val from = DateFormatter.startOfDayMillis(now)
        val to = DateFormatter.endOfDayMillis(now)
        val omzet = txRepo.omzetInRange(from, to)
        val count = txRepo.countInRange(from, to)
        val avg = if (count > 0) omzet / count else 0L
        val top = txRepo.topProducts(from, to, limit = 5)
        _state.value = _state.value.copy(
            today = TodaySummary(omzet, count, avg),
            topProducts = top,
        )
    }
}
