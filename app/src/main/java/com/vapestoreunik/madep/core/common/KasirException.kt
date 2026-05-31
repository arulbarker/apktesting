package com.vapestoreunik.madep.core.common

sealed class KasirException(message: String) : Exception(message) {
    class StockInsufficient(val variantName: String, val available: Int, val requested: Int) :
        KasirException("Stok $variantName tidak cukup (tersedia $available, diminta $requested)")

    class InvalidPin(val attemptsLeft: Int) :
        KasirException("PIN salah. Sisa $attemptsLeft percobaan")

    class PinLockedOut(val remainingSeconds: Int) :
        KasirException("Terkunci, coba lagi dalam $remainingSeconds detik")

    class DuplicateBarcode(val sku: String) :
        KasirException("Barcode $sku sudah dipakai produk lain")

    class CategoryHasProducts(val name: String) :
        KasirException("Kategori $name masih punya produk")

    class ProductHasTransactions(val name: String) :
        KasirException("Produk $name punya riwayat transaksi")

    class BackupFileInvalid : KasirException("File backup tidak valid")
    class BackupSchemaIncompatible :
        KasirException("Versi backup tidak kompatibel dengan aplikasi")

    class StorageWriteFailed(cause: Throwable) :
        KasirException("Gagal menulis ke penyimpanan") {
        init {
            initCause(cause)
        }
    }

    class CartEmpty : KasirException("Keranjang kosong")
    class PaymentInsufficient(val total: Long, val paid: Long) :
        KasirException("Pembayaran kurang Rp ${total - paid}")

    class DiscountExceedsSubtotal : KasirException("Diskon melebihi subtotal")
}
