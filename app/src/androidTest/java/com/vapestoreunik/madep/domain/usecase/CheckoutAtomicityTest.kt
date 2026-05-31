package com.vapestoreunik.madep.domain.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.local.KasirDatabase
import com.vapestoreunik.madep.data.local.buildInMemoryDb
import com.vapestoreunik.madep.data.local.entity.CategoryEntity
import com.vapestoreunik.madep.data.local.entity.ProductEntity
import com.vapestoreunik.madep.data.local.entity.VariantEntity
import com.vapestoreunik.madep.data.repository.DefaultTransactionRepository
import com.vapestoreunik.madep.data.repository.RoomTransactor
import com.vapestoreunik.madep.domain.model.Cart
import com.vapestoreunik.madep.domain.model.CartItem
import com.vapestoreunik.madep.domain.model.PaymentInput
import com.vapestoreunik.madep.domain.model.PaymentMethod
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckoutAtomicityTest {
    private lateinit var db: KasirDatabase
    private lateinit var uc: CheckoutUseCase
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
                stock = 5,
                createdAt = now,
                updatedAt = now,
            ),
        )
        val txRepo = DefaultTransactionRepository(db.transactionDao())
        uc = CheckoutUseCase(
            txRepo = txRepo,
            transactionDao = db.transactionDao(),
            variantDao = db.variantDao(),
            stockLogDao = db.stockLogDao(),
            calculate = CalculateTotalsUseCase(),
            transactor = RoomTransactor(db),
        )
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun stockInsufficient_rolls_back_all() = runBlocking {
        // Request 10, available 5
        val cart = Cart(items = listOf(CartItem(variantId, "Hero7", "", 75000, 10, 5)))
        val r = uc.execute(cart, PaymentInput(PaymentMethod.TUNAI, 750000), taxPercent = 0)
        assertTrue(r.isFailure)
        assertTrue(r.exceptionOrNull() is KasirException.StockInsufficient)

        // Stock harus tetap 5, tidak ada transaction tersisa
        assertEquals(5, db.variantDao().stockOf(variantId))
        assertEquals(0, db.transactionDao().countInRange(0, Long.MAX_VALUE))
    }

    @Test
    fun successful_checkout_persists_everything() = runBlocking {
        val cart = Cart(items = listOf(CartItem(variantId, "Hero7", "", 75000, 2, 5)))
        val r = uc.execute(cart, PaymentInput(PaymentMethod.TUNAI, 150000), taxPercent = 0)
        assertTrue(r.isSuccess)
        assertEquals(3, db.variantDao().stockOf(variantId))
        assertEquals(1, db.transactionDao().countInRange(0, Long.MAX_VALUE))
        val txId = r.getOrThrow()
        assertEquals(1, db.transactionDao().itemsOf(txId).size)
    }
}
