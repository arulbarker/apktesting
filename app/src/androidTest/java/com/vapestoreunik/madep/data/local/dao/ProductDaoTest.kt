package com.vapestoreunik.madep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vapestoreunik.madep.data.local.KasirDatabase
import com.vapestoreunik.madep.data.local.buildInMemoryDb
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var db: KasirDatabase
    private lateinit var pdao: ProductDao
    private lateinit var cdao: CategoryDao
    private var catId: Long = 0L

    @Before
    fun setup() = runBlocking {
        db = buildInMemoryDb()
        pdao = db.productDao()
        cdao = db.categoryDao()
        catId = cdao.insert(CategoryEntity(name = "Liquid"))
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insertAndFilterByCategory() = runTest {
        val now = System.currentTimeMillis()
        pdao.insert(ProductEntity(name = "Hero7 Mango", categoryId = catId, createdAt = now, updatedAt = now))
        pdao.insert(ProductEntity(name = "Drag X", categoryId = catId, createdAt = now, updatedAt = now))
        val byCat = pdao.observeFiltered("", catId).first()
        assertEquals(2, byCat.size)
    }

    @Test
    fun searchByQuery() = runTest {
        val now = System.currentTimeMillis()
        pdao.insert(ProductEntity(name = "Hero7 Mango Ice", categoryId = catId, createdAt = now, updatedAt = now))
        pdao.insert(ProductEntity(name = "Hero7 Strawberry", categoryId = catId, createdAt = now, updatedAt = now))
        val mango = pdao.observeFiltered("mango", null).first()
        assertEquals(1, mango.size)
        assertEquals("Hero7 Mango Ice", mango[0].name)
    }

    @Test
    fun inactiveExcludedFromFiltered() = runTest {
        val now = System.currentTimeMillis()
        val id = pdao.insert(ProductEntity(name = "Hero7", categoryId = catId, isActive = false, createdAt = now, updatedAt = now))
        val all = pdao.observeFiltered("", null).first()
        assertTrue(all.none { it.id == id })
    }
}
