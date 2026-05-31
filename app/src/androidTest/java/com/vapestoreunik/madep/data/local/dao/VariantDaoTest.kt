package com.vapestoreunik.madep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vapestoreunik.madep.data.local.KasirDatabase
import com.vapestoreunik.madep.data.local.buildInMemoryDb
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VariantDaoTest {
    private lateinit var db: KasirDatabase
    private lateinit var vdao: VariantDao
    private var productId: Long = 0L

    @Before
    fun setup() = runBlocking {
        db = buildInMemoryDb()
        val now = System.currentTimeMillis()
        val catId = db.categoryDao().insert(CategoryEntity(name = "Liquid"))
        productId = db.productDao().insert(
            ProductEntity(
                name = "Hero7",
                categoryId = catId,
                hasVariants = true,
                createdAt = now,
                updatedAt = now,
            ),
        )
        vdao = db.variantDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun adjustStockDelta() = runTest {
        val now = System.currentTimeMillis()
        val id = vdao.insert(
            VariantEntity(
                productId = productId,
                name = "30ml 3mg",
                price = 75000,
                stock = 10,
                createdAt = now,
                updatedAt = now,
            ),
        )
        vdao.adjustStock(id, -3, now)
        assertEquals(7, vdao.stockOf(id))
        vdao.adjustStock(id, 5, now)
        assertEquals(12, vdao.stockOf(id))
    }

    @Test
    fun observeLowStock() = runTest {
        val now = System.currentTimeMillis()
        vdao.insert(
            VariantEntity(
                productId = productId,
                name = "low",
                price = 1000,
                stock = 2,
                lowStockThreshold = 5,
                createdAt = now,
                updatedAt = now,
            ),
        )
        vdao.insert(
            VariantEntity(
                productId = productId,
                name = "ok",
                price = 1000,
                stock = 20,
                lowStockThreshold = 5,
                createdAt = now,
                updatedAt = now,
            ),
        )
        val low = vdao.observeLowStock(10).first()
        assertEquals(1, low.size)
        assertEquals("low", low[0].name)
    }
}
