package com.vapestoreunik.madep.data.repository

import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.local.dao.CategoryDao
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<CategoryEntity>>
    suspend fun upsert(category: CategoryEntity): Long
    suspend fun delete(category: CategoryEntity): Result<Unit>
    suspend fun seedDefaults()
}

class DefaultCategoryRepository @Inject constructor(
    private val dao: CategoryDao,
) : CategoryRepository {
    override fun observeAll() = dao.observeAll()

    override suspend fun upsert(category: CategoryEntity): Long =
        if (category.id == 0L) {
            dao.insert(category)
        } else {
            dao.update(category)
            category.id
        }

    override suspend fun delete(category: CategoryEntity): Result<Unit> {
        val count = dao.productCount(category.id)
        return if (count > 0) {
            Result.failure(KasirException.CategoryHasProducts(category.name))
        } else {
            runCatching { dao.delete(category) }
        }
    }

    override suspend fun seedDefaults() {
        val defaults = listOf(
            "Liquid",
            "Device/Mod",
            "Coil & Atomizer",
            "Cotton",
            "Baterai",
            "Aksesoris",
        )
        defaults.forEachIndexed { i, name ->
            dao.insert(CategoryEntity(name = name, sortOrder = i))
        }
    }
}
