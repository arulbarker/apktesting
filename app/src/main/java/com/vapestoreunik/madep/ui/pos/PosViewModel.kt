package com.vapestoreunik.madep.ui.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import com.vapestoreunik.madep.data.repository.CategoryRepository
import com.vapestoreunik.madep.data.repository.ProductRepository
import com.vapestoreunik.madep.domain.model.Cart
import com.vapestoreunik.madep.domain.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VariantPickerInfo(val product: ProductEntity, val variants: List<VariantEntity>)

data class PosState(
    val products: List<ProductEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val query: String = "",
    val selectedCategoryId: Long? = null,
    val cart: Cart = Cart(),
    val variantPicker: VariantPickerInfo? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PosViewModel @Inject constructor(
    private val productRepo: ProductRepository,
    categoryRepo: CategoryRepository,
    private val cartHolder: CartHolder,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _categoryId = MutableStateFlow<Long?>(null)
    private val _picker = MutableStateFlow<VariantPickerInfo?>(null)

    private val products = combine(_query, _categoryId) { q, c -> q to c }
        .flatMapLatest { (q, c) -> productRepo.observeFiltered(q, c) }

    val state: StateFlow<PosState> = combine(
        products,
        categoryRepo.observeAll(),
        _query,
        _categoryId,
        cartHolder.cart,
        _picker,
    ) { arr ->
        @Suppress("UNCHECKED_CAST")
        PosState(
            products = arr[0] as List<ProductEntity>,
            categories = arr[1] as List<CategoryEntity>,
            query = arr[2] as String,
            selectedCategoryId = arr[3] as Long?,
            cart = arr[4] as Cart,
            variantPicker = arr[5] as VariantPickerInfo?,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PosState())

    fun setQuery(q: String) { _query.value = q }
    fun setCategory(id: Long?) { _categoryId.value = id }

    fun onProductTap(product: ProductEntity) = viewModelScope.launch {
        val variants = productRepo.getVariantsOf(product.id).filter { it.isActive && it.stock > 0 }
        when {
            variants.isEmpty() -> Unit  // habis — no-op
            !product.hasVariants || variants.size == 1 -> addToCart(product, variants.first())
            else -> _picker.value = VariantPickerInfo(product, variants)
        }
    }

    fun onVariantPicked(product: ProductEntity, variant: VariantEntity) {
        addToCart(product, variant)
        _picker.value = null
    }

    fun dismissPicker() { _picker.value = null }

    private fun addToCart(product: ProductEntity, variant: VariantEntity) {
        cartHolder.set(
            cartHolder.cart.value.addItem(
                CartItem(
                    variantId = variant.id,
                    productName = product.name,
                    variantName = variant.name,
                    unitPrice = variant.price,
                    qty = 1,
                    availableStock = variant.stock,
                ),
            ),
        )
    }

    fun updateQty(variantId: Long, qty: Int) {
        cartHolder.set(cartHolder.cart.value.updateQty(variantId, qty))
    }

    fun removeItem(variantId: Long) {
        cartHolder.set(cartHolder.cart.value.removeItem(variantId))
    }
}
