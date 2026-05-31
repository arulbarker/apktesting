package com.vapestoreunik.madep.domain.usecase

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.vapestoreunik.madep.core.common.DateFormatter
import com.vapestoreunik.madep.core.common.RupiahFormatter
import com.vapestoreunik.madep.domain.model.ReceiptData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Generate struk PDF lebar ~164pt (58mm equivalent), tinggi dinamis.
 * Saved to `cacheDir/receipts/<code>.pdf`, di-share via FileProvider (lihat ReceiptScreen).
 */
class BuildReceiptPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val PAGE_WIDTH = 164    // 58mm @ 72 DPI ≈ 164pt
        const val MARGIN = 8
        const val LINE_HEIGHT = 12
    }

    suspend fun execute(data: ReceiptData): Result<File> = runCatching {
        val pdf = PdfDocument()
        val paint = Paint().apply { textSize = 8f; isAntiAlias = true }
        val bold = Paint(paint).apply { isFakeBoldText = true }

        val totalLines = 6 + data.lines.size * 2 + 8 + data.footer.lines().size
        val pageHeight = MARGIN * 2 + totalLines * LINE_HEIGHT
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, pageHeight, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        var y = MARGIN.toFloat() + LINE_HEIGHT
        fun line(text: String, p: Paint = paint, center: Boolean = false) {
            if (center) {
                val w = p.measureText(text)
                canvas.drawText(text, (PAGE_WIDTH - w) / 2, y, p)
            } else {
                canvas.drawText(text, MARGIN.toFloat(), y, p)
            }
            y += LINE_HEIGHT
        }

        line(data.storeName, bold, center = true)
        if (data.storeAddress.isNotBlank()) line(data.storeAddress, center = true)
        if (data.storePhone.isNotBlank()) line(data.storePhone, center = true)
        line("--------------------------------")
        line("${data.transactionCode}   ${DateFormatter.formatDisplay(data.createdAtMillis)} ${DateFormatter.formatTime(data.createdAtMillis)}")
        line("--------------------------------")

        data.lines.forEach { l ->
            line("${l.productName} ${l.variantName}".trim())
            line("  ${l.qty} x ${RupiahFormatter.format(l.unitPrice)}   ${RupiahFormatter.format(l.subtotal)}")
        }
        line("--------------------------------")
        line("Subtotal:          ${RupiahFormatter.format(data.subtotal)}")
        if (data.discountAmount > 0) line("Diskon:           -${RupiahFormatter.format(data.discountAmount)}")
        if (data.taxAmount > 0) line("Pajak:             ${RupiahFormatter.format(data.taxAmount)}")
        line("TOTAL:             ${RupiahFormatter.format(data.total)}", bold)
        line("Bayar (${data.paymentMethod}): ${RupiahFormatter.format(data.paid)}")
        if (data.change > 0) line("Kembalian:         ${RupiahFormatter.format(data.change)}")
        line("--------------------------------")
        data.footer.lines().forEach { line(it, center = true) }

        pdf.finishPage(page)
        val outDir = File(context.cacheDir, "receipts").apply { mkdirs() }
        val file = File(outDir, "${data.transactionCode}.pdf")
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        file
    }
}
