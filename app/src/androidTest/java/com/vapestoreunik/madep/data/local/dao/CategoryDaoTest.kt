package com.vapestoreunik.madep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vapestoreunik.madep.data.local.KasirDatabase
import com.vapestoreunik.madep.data.local.buildInMemoryDb
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {
    private lateinit var db: KasirDatabase
    private lateinit var dao: CategoryDao

    @Before
    fun setup() {
        db = buildInMemoryDb()
        dao = db.categoryDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insertAndObserveAll() = runTest {
        val id = dao.insert(CategoryEntity(name = "Liquid", sortOrder = 1))
        val all = dao.observeAll().first()
        assertEquals(1, all.size)
        assertEquals(id, all[0].id)
        assertEquals("Liquid", all[0].name)
    }

    @Test
    fun sortByOrderThenName() = runTest {
        dao.insert(CategoryEntity(name = "Coil", sortOrder = 2))
        dao.insert(CategoryEntity(name = "Liquid", sortOrder = 1))
        val all = dao.observeAll().first()
        assertEquals(listOf("Liquid", "Coil"), all.map { it.name })
    }

    @Test
    fun productCountIsZeroForNewCategory() = runTest {
        val id = dao.insert(CategoryEntity(name = "Liquid"))
        assertEquals(0, dao.productCount(id))
    }
}
