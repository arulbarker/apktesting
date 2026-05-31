package com.vapestoreunik.madep.domain.usecase

import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.repository.FakeStockLogDao
import com.vapestoreunik.madep.data.repository.FakeTransactionDao
import com.vapestoreunik.madep.data.repository.FakeTransactionRepository
import com.vapestoreunik.madep.data.repository.FakeVariantDao
import com.vapestoreunik.madep.data.repository.NoopTransactor
import com.vapestoreunik.madep.domain.model.Cart
import com.vapestoreunik.madep.domain.model.CartItem
import com.vapestoreunik.madep.domain.model.PaymentInput
import com.vapestoreunik.madep.domain.model.PaymentMethod
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckoutUseCaseTest {
    private val fixedClock = { 1780272000000L }  // 2026-06-01 00:00 UTC

    private fun item(variantId: Long, qty: Int, price: Long, available: Int = 100) =
        CartItem(variantId, "Produk", "Var", price, qty, available)

    private fun makeUseCase(
        initialStocks: Map<Long, Int>,
        nextSeq: (String) -> Int = { 1 },
    ): Triple<CheckoutUseCase, FakeTransactionRepository, Pair<FakeTransactionDao, FakeStockLogDao>> {
        val txRepo = FakeTransactionRepository(nextSeq)
        val txDao = FakeTransactionDao()
        val variantDao = FakeVariantDao(initialStocks)
        val stockLogDao = FakeStockLogDao()
        val uc = CheckoutUseCase(
            txRepo = txRepo,
            transactionDao = txDao,
            variantDao = variantDao,
            stockLogDao = stockLogDao,
            calculate = CalculateTotalsUseCase(),
            transactor = NoopTransactor(),
            clock = fixedClock,
        )
        return Triple(uc, txRepo, txDao to stockLogDao)
    }

    @Test fun successful_checkout_decrements_stock_and_logs() = runTest {
        val (uc, _, daos) = makeUseCase(mapOf(1L to 10))
        val (_, stockLogDao) = daos

        val cart = Cart(items = listOf(item(1, 3, 25000)))
        val result = uc.execute(cart, PaymentInput(PaymentMethod.TUNAI, 100000), taxPercent = 0)

        assertTrue(result.isSuccess)
        assertEquals(1, stockLogDao.logs.size)
        assertEquals(-3, stockLogDao.logs[0].qty)
        assertEquals(7, stockLogDao.logs[0].balanceAfter)
    }

    @Test fun generates_code_TRX_yyyymmdd_seq() = runTest {
        val (uc, _, daos) = makeUseCase(mapOf(1L to 10), nextSeq = { 42 })
        val (txDao, _) = daos

        uc.execute(
            Cart(items = listOf(item(1, 1, 25000))),
            PaymentInput(PaymentMethod.TUNAI, 25000),
            0,
        )
        assertEquals("TRX-20260601-0042", txDao.insertedTransactions.first().code)
    }

    @Test fun stock_insufficient_throws() = runTest {
        val (uc, _, _) = makeUseCase(mapOf(1L to 2))
        val r = uc.execute(
            Cart(items = listOf(item(1, 5, 25000))),
            PaymentInput(PaymentMethod.TUNAI, 125000),
            0,
        )
        assertTrue(r.isFailure)
        assertTrue(r.exceptionOrNull() is KasirException.StockInsufficient)
    }

    @Test fun snapshot_fields_copied_at_checkout() = runTest {
        val (uc, _, daos) = makeUseCase(mapOf(1L to 10))
        val (txDao, _) = daos

        val cart = Cart(items = listOf(CartItem(1L, "Hero7 Mango", "30ml 3mg", 75000, 2, 10)))
        uc.execute(cart, PaymentInput(PaymentMethod.TUNAI, 150000), 0)

        val item = txDao.insertedItems.first()
        assertEquals("Hero7 Mango", item.productNameSnapshot)
        assertEquals("30ml 3mg", item.variantNameSnapshot)
        assertEquals(75000L, item.priceSnapshot)
    }
}
