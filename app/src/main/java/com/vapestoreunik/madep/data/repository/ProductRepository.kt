package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.local.dao.ProductDao
import com.vapestoreunik.madep.data.local.dao.VariantDao
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

data class ProductWithVariants(val product: ProductEntity, val variants: List<VariantEntity>)

interface ProductRepository {
    fun observeFiltered(query: String, categoryId: Long?): Flow<List<ProductEntity>>
    fun observeVariants(productId: Long): Flow<List<VariantEntity>>
    fun observeLowStock(limit: Int = 5): Flow<List<VariantEntity>>
    suspend fun getById(id: Long): ProductEntity?
    suspend fun getVariantsOf(productId: Long): List<VariantEntity>
    suspend fun saveProductWithVariants(
        product: ProductEntity,
        variants: List<VariantEntity>,
    ): Result<Long>
    suspend fun setActive(productId: Long, active: Boolean): Result<Unit>
}

class DefaultProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val variantDao: VariantDao,
) : ProductRepository {
    override fun observeFiltered(query: String, categoryId: Long?) =
        productDao.observeFiltered(query, categoryId)

    override fun observeVariants(productId: Long) = variantDao.observeByProduct(productId)
    override fun observeLowStock(limit: Int) = variantDao.observeLowStock(limit)
    override suspend fun getById(id: Long) = productDao.getById(id)
    override suspend fun getVariantsOf(productId: Long) = variantDao.getAllByProduct(productId)

    override suspend fun saveProductWithVariants(
        product: ProductEntity,
        variants: List<VariantEntity>,
    ): Result<Long> = runCatching {
        require(variants.isNotEmpty()) { "Minimal 1 varian (default jika tidak punya varian)" }
        require(product.name.isNotBlank()) { "Nama produk wajib" }
        val now = System.currentTimeMillis()
        val pid = if (product.id == 0L) {
            productDao.insert(product.copy(createdAt = now, updatedAt = now))
        } else {
            productDao.update(product.copy(updatedAt = now))
            product.id
        }
        variants.forEach { v ->
            val withProduct = v.copy(
                productId = pid,
                updatedAt = now,
                createdAt = if (v.id == 0L) now else v.createdAt,
            )
            if (v.id == 0L) variantDao.insert(withProduct) else variantDao.update(withProduct)
        }
        pid
    }

    override suspend fun setActive(productId: Long, active: Boolean): Result<Unit> = runCatching {
        if (!active) {
            val txCount = productDao.transactionCountFor(productId)
            if (txCount > 0) {
                val p = productDao.getById(productId)
                throw KasirException.ProductHasTransactions(p?.name ?: "produk")
            }
        }
        productDao.setActive(productId, active, System.currentTimeMillis())
    }
}
