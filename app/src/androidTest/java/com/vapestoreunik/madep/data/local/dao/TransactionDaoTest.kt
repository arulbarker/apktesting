package com.vapestoreunik.madep.data.local.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vapestoreunik.madep.data.local.KasirDatabase
import com.vapestoreunik.madep.data.local.buildInMemoryDb
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.local.entity.TransactionItemEntity
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {
    private lateinit var db: KasirDatabase
    private lateinit var tdao: TransactionDao
    private var variantId: Long = 0L

    @Before
    fun setup() = runBlocking {
        db = buildInMemoryDb()
        val now = System.currentTimeMillis()
        val catId = db.categoryDao().insert(CategoryEntity(name = "Liquid"))
        val pid = db.productDao().insert(
            ProductEntity(name = "Hero7", categoryId = catId, createdAt = now, updatedAt = now),
        )
        variantId = db.variantDao().insert(
            VariantEntity(
                productId = pid,
                name = "",
                price = 75000,
                stock = 10,
                createdAt = now,
                updatedAt = now,
            ),
        )
        tdao = db.transactionDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insertTransactionAndItems_cascadeDelete() = runTest {
        val now = System.currentTimeMillis()
        val txId = tdao.insertTransaction(
            TransactionEntity(
                code = "TRX-20260601-0001",
                subtotal = 75000,
                total = 75000,
                paid = 100000,
                change = 25000,
                paymentMethod = "TUNAI",
                createdAt = now,
            ),
        )
        tdao.insertItems(
            listOf(
                TransactionItemEntity(
                    transactionId = txId,
                    variantId = variantId,
                    productNameSnapshot = "Hero7",
                    variantNameSnapshot = "",
                    priceSnapshot = 75000,
                    qty = 1,
                    subtotal = 75000,
                ),
            ),
        )
        assertEquals(1, tdao.itemsOf(txId).size)

        // Cascade delete via direct execSQL
        db.openHelper.writableDatabase.execSQL("DELETE FROM transactions WHERE id = $txId")
        assertEquals(0, tdao.itemsOf(txId).size)
    }

    @Test
    fun rangeQuery_omzetSumsCorrectly() = runTest {
        val now = System.currentTimeMillis()
        tdao.insertTransaction(
            TransactionEntity(
                code = "TRX-20260601-0001",
                subtotal = 50000,
                total = 50000,
                paid = 50000,
                change = 0,
                paymentMethod = "TUNAI",
                createdAt = now,
            ),
        )
        tdao.insertTransaction(
            TransactionEntity(
                code = "TRX-20260601-0002",
                subtotal = 30000,
                total = 30000,
                paid = 30000,
                change = 0,
                paymentMethod = "NON_TUNAI",
                createdAt = now,
            ),
        )
        val omzet = tdao.omzetInRange(now - 1000, now + 1000)
        assertEquals(80000L, omzet)
    }

    @Test
    fun uniqueCodeConstraint() = runTest {
        val now = System.currentTimeMillis()
        tdao.insertTransaction(
            TransactionEntity(
                code = "TRX-X-1",
                subtotal = 1,
                total = 1,
                paid = 1,
                change = 0,
                paymentMethod = "TUNAI",
                createdAt = now,
            ),
        )
        try {
            tdao.insertTransaction(
                TransactionEntity(
                    code = "TRX-X-1",
                    subtotal = 1,
                    total = 1,
                    paid = 1,
                    change = 0,
                    paymentMethod = "TUNAI",
                    createdAt = now,
                ),
            )
            fail("Expected unique constraint violation")
        } catch (e: SQLiteConstraintException) {
            // expected
        }
    }
}
