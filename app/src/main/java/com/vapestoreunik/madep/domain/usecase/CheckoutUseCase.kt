package com.vapestoreunik.madep.domain.usecase

import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.KasirException
import com.vapestoreunik.madep.data.local.dao.StockLogDao
import com.vapestoreunik.madep.data.local.dao.TransactionDao
import com.vapestoreunik.madep.data.local.dao.VariantDao
import com.vapestoreunik.madep.data.local.entity.StockLogEntity
import com.vapestoreunik.madep.data.local.entity.TransactionEntity
import com.vapestoreunik.madep.data.local.entity.TransactionItemEntity
import com.vapestoreunik.madep.data.repository.Transactor
import com.vapestoreunik.madep.data.repository.TransactionRepository
import com.vapestoreunik.madep.domain.model.Cart
import com.vapestoreunik.madep.domain.model.PaymentInput
import javax.inject.Inject

class CheckoutUseCase @Inject constructor(
    private val txRepo: TransactionRepository,
    private val transactionDao: TransactionDao,
    private val variantDao: VariantDao,
    private val stockLogDao: StockLogDao,
    private val calculate: CalculateTotalsUseCase,
    private val transactor: Transactor,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend fun execute(cart: Cart, payment: PaymentInput, taxPercent: Int): Result<Long> = runCatching {
        val totals = calculate.execute(cart, taxPercent, payment.paid).getOrThrow()
        val now = clock()

        transactor.withTx {
            // 1. Re-check stock untuk setiap item (race protection)
            cart.items.forEach { item ->
                val current = variantDao.stockOf(item.variantId)
                    ?: throw IllegalStateException("Varian ${item.variantName} tidak ditemukan")
                if (current < item.qty) {
                    throw KasirException.StockInsufficient(item.variantName, current, item.qty)
                }
            }

            // 2. Generate kode TRX-YYYYMMDD-NNNN
            val yyyymmdd = DateFormatter.formatYyyymmdd(now)
            val seq = txRepo.nextSequenceForDay(yyyymmdd)
            val code = "TRX-$yyyymmdd-${seq.toString().padStart(4, '0')}"

            // 3. Insert transaction
            val txId = transactionDao.insertTransaction(
                TransactionEntity(
                    code = code,
                    subtotal = totals.subtotal,
                    discountType = cart.discount?.type?.name,
                    discountValue = cart.discount?.value ?: 0L,
                    discountAmount = totals.discountAmount,
                    taxPercent = taxPercent,
                    taxAmount = totals.taxAmount,
                    total = totals.total,
                    paid = payment.paid,
                    change = totals.change,
                    paymentMethod = payment.method.raw,
                    note = cart.note,
                    createdAt = now,
                ),
            )

            // 4. Insert items dengan snapshot fields
            transactionDao.insertItems(
                cart.items.map {
                    TransactionItemEntity(
                        transactionId = txId,
                        variantId = it.variantId,
                        productNameSnapshot = it.productName,
                        variantNameSnapshot = it.variantName,
                        priceSnapshot = it.unitPrice,
                        qty = it.qty,
                        subtotal = it.subtotal,
                    )
                },
            )

            // 5. Update stok + insert log per item
            cart.items.forEach { item ->
                variantDao.adjustStock(item.variantId, -item.qty, now)
                val newBalance = variantDao.stockOf(item.variantId) ?: 0
                stockLogDao.insert(
                    StockLogEntity(
                        variantId = item.variantId,
                        type = "SALE",
                        qty = -item.qty,
                        balanceAfter = newBalance,
                        refType = "TRANSACTION",
                        refId = txId,
                        note = null,
                        createdAt = now,
                    ),
                )
            }
            txId
        }
    }
}
