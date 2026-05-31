package com.vapestoreunik.madep.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import com.vapestoreunik.madep.data.repository.CategoryRepository
import com.vapestoreunik.madep.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class VariantDraft(
    val id: Long = 0L,
    val name: String = "",
    val sku: String = "",
    val barcode: String = "",
    val price: Long = 0L,
    val stock: Int = 0,
    val lowStockThreshold: Int = 5,
)

data class ProductFormState(
    val productId: Long? = null,
    val name: String = "",
    val brand: String = "",
    val description: String = "",
    val categoryId: Long? = null,
    val hasVariants: Boolean = false,
    val imageUri: String? = null,
    val variants: List<VariantDraft> = listOf(VariantDraft()),
    val categories: List<CategoryEntity> = emptyList(),
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val productRepo: ProductRepository,
    private val categoryRepo: CategoryRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProductFormState())
    val state = _state.asStateFlow()

    fun load(productId: Long?) = viewModelScope.launch {
        val categories = categoryRepo.observeAll().first()
        if (productId == null) {
            _state.value = ProductFormState(
                categories = categories,
                categoryId = categories.firstOrNull()?.id,
            )
        } else {
            val p = productRepo.getById(productId) ?: return@launch
            val variants = productRepo.getVariantsOf(productId)
            _state.value = ProductFormState(
                productId = p.id,
                name = p.name,
                brand = p.brand.orEmpty(),
                description = p.description.orEmpty(),
                categoryId = p.categoryId,
                hasVariants = p.hasVariants,
                imageUri = p.imageUri,
                variants = variants.map { v ->
                    VariantDraft(
                        v.id, v.name, v.sku.orEmpty(), v.barcode.orEmpty(),
                        v.price, v.stock, v.lowStockThreshold,
                    )
                }.ifEmpty { listOf(VariantDraft()) },
                categories = categories,
            )
        }
    }

    fun setName(v: String) { _state.value = _state.value.copy(name = v, error = null) }
    fun setBrand(v: String) { _state.value = _state.value.copy(brand = v) }
    fun setDescription(v: String) { _state.value = _state.value.copy(description = v) }
    fun setCategory(id: Long) { _state.value = _state.value.copy(categoryId = id) }

    fun toggleHasVariants(value: Boolean) {
        _state.value = _state.value.copy(
            hasVariants = value,
            variants = if (value) _state.value.variants
            else listOf(_state.value.variants.firstOrNull() ?: VariantDraft()),
        )
    }

    fun addVariant() {
        _state.value = _state.value.copy(variants = _state.value.variants + VariantDraft())
    }

    fun removeVariant(index: Int) {
        if (_state.value.variants.size <= 1) return
        _state.value = _state.value.copy(
            variants = _state.value.variants.toMutableList().apply { removeAt(index) },
        )
    }

    fun updateVariant(index: Int, transform: (VariantDraft) -> VariantDraft) {
        val list = _state.value.variants.toMutableList()
        list[index] = transform(list[index])
        _state.value = _state.value.copy(variants = list)
    }

    fun save() = viewModelScope.launch {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.value = s.copy(error = "Nama produk wajib"); return@launch
        }
        if (s.categoryId == null) {
            _state.value = s.copy(error = "Pilih kategori"); return@launch
        }
        if (s.variants.any { it.price <= 0 }) {
            _state.value = s.copy(error = "Harga setiap varian harus > 0"); return@launch
        }
        val now = System.currentTimeMillis()
        val product = ProductEntity(
            id = s.productId ?: 0L,
            name = s.name.trim(),
            categoryId = s.categoryId,
            brand = s.brand.ifBlank { null },
            description = s.description.ifBlank { null },
            imageUri = s.imageUri,
            hasVariants = s.hasVariants,
            isActive = true,
            createdAt = now,
            updatedAt = now,
        )
        val variants = s.variants.map { d ->
            VariantEntity(
                id = d.id,
                productId = s.productId ?: 0L,
                name = if (s.hasVariants) d.name else "",
                sku = d.sku.ifBlank { null },
                barcode = d.barcode.ifBlank { null },
                price = d.price,
                stock = d.stock,
                lowStockThreshold = d.lowStockThreshold,
                isActive = true,
                createdAt = now,
                updatedAt = now,
            )
        }
        productRepo.saveProductWithVariants(product, variants)
            .onSuccess { _state.value = s.copy(saved = true) }
            .onFailure { _state.value = s.copy(error = it.message ?: "Gagal menyimpan") }
    }
}
