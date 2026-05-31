package com.vapestoreunik.madep.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.repository.CategoryRepository
import com.vapestoreunik.madep.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class ProductListState(
    val products: List<ProductEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val query: String = "",
    val selectedCategoryId: Long? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    productRepo: ProductRepository,
    categoryRepo: CategoryRepository,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _categoryId = MutableStateFlow<Long?>(null)

    private val products = combine(_query, _categoryId) { q, c -> q to c }
        .flatMapLatest { (q, c) -> productRepo.observeFiltered(q, c) }

    val state: StateFlow<ProductListState> = combine(
        products,
        categoryRepo.observeAll(),
        _query,
        _categoryId,
    ) { p, c, q, cid -> ProductListState(p, c, q, cid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductListState())

    fun setQuery(q: String) { _query.value = q }
    fun setCategory(id: Long?) { _categoryId.value = id }
}
