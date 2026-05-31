# Manual Test Checklist — Kasir Vapestore Tier A

Sebelum release/install di device produksi, jalankan checklist berikut.

## First Launch
- [ ] Uninstall, lalu install ulang. Setup wizard muncul.
- [ ] Step 1: nama toko wajib (blank → error muncul).
- [ ] Step 2: PIN length 4-6, konfirmasi harus cocok.
- [ ] Step 3: "Mulai Pakai" → ke PIN login.

## PIN Login
- [ ] PIN 4 digit → tap OK → masuk dashboard.
- [ ] PIN 6 digit → auto-verify pada digit ke-6 → masuk dashboard.
- [ ] PIN salah → "PIN salah. Sisa X percobaan".
- [ ] 5 PIN salah → "Terkunci 30 detik", NumPad disabled, countdown jalan.
- [ ] Setelah 30 detik countdown selesai → bisa input lagi.
- [ ] Tap "Kunci" di Settings → kembali ke PIN screen.

## Setup → PIN → Main Flow
- [ ] First-time: SetupWizard → PinLogin → Dashboard
- [ ] Subsequent app open: langsung PinLogin (skip wizard)
- [ ] Tab Beranda, Kasir, Produk, Riwayat berfungsi
- [ ] Gear icon → Settings screen

## POS / Kasir
- [ ] Tab Kasir menampilkan grid produk dengan search bar + chip kategori
- [ ] Search berdasarkan nama produk/brand bekerja
- [ ] Filter kategori chip bekerja
- [ ] Produk dengan `hasVariants=false` → tap langsung +1 ke cart
- [ ] Produk dengan `hasVariants=true` (multi varian aktif) → bottom sheet varian muncul
- [ ] Produk dengan stok 0 di semua varian → tap tidak menambah cart (silent)
- [ ] FAB Cart muncul saat cart ada item, menampilkan total + subtotal
- [ ] Tap FAB cart → bottom sheet keranjang muncul
- [ ] QtyStepper di bottom sheet bekerja (max = availableStock)
- [ ] Tombol Hapus per item bekerja
- [ ] Tombol Bayar disabled saat cart kosong

## Checkout
- [ ] Subtotal benar (sum unitPrice × qty)
- [ ] Diskon "Tidak" → tidak dikurangi
- [ ] Diskon "Persen" 10% × Rp 12.345 → Rp 1.235 dikurangi
- [ ] Diskon "Nominal" melebihi subtotal → total negative dicegah (error)
- [ ] PPN enabled di Settings → tax muncul di Checkout, applied setelah diskon
- [ ] Tunai: paid < total → tombol disabled
- [ ] Tunai: paid > total → kembalian muncul
- [ ] Non-Tunai: paid auto-set ke total
- [ ] Selesaikan → navigate ke Receipt, stok berkurang sesuai qty
- [ ] Tap "Bayar" 2x cepat tidak buat 2 transaksi (UI guard)

## Struk / Receipt
- [ ] Struk berisi: nama toko, alamat, no struk, tanggal, items, total, payment, kembalian, footer
- [ ] Code transaksi format: TRX-YYYYMMDD-NNNN
- [ ] Snapshot fields: produk yang sudah di-rename masih tampil dengan nama asli di struk lama
- [ ] Tap Share icon → app chooser muncul, PDF terkirim (cek file di share target)
- [ ] "Transaksi Baru" → kembali ke MainScaffold (Kasir tab dengan cart kosong)

## Produk
- [ ] Tab Produk: grid dengan search + filter kategori
- [ ] FAB + → ProductForm baru
- [ ] Tambah produk tanpa varian → 1 default variant dibuat otomatis (kosong nama)
- [ ] Tambah produk dengan varian (toggle ON) → bisa tambah/hapus varian inline
- [ ] Validasi: nama wajib, kategori wajib, harga > 0
- [ ] Edit produk → load existing variants, simpan update

## Kategori
- [ ] Settings → Kelola Kategori → list kategori
- [ ] FAB + → tambah kategori
- [ ] Icon Edit per item → rename dialog
- [ ] Icon Hapus per item → hapus (kalau ada produk → error "kategori masih punya produk")

## Riwayat
- [ ] Tab Riwayat: default tampilkan transaksi hari ini
- [ ] Filter Tunai/Non-Tunai bekerja
- [ ] Tap transaksi → Receipt screen (read-only)

## Laporan Harian
- [ ] Settings → Laporan ATAU Dashboard "Lihat Laporan Detail" → ReportsScreen
- [ ] Chip tanggal → DatePicker untuk pilih hari lain
- [ ] Omzet hari ini benar (cross-check dengan list riwayat)
- [ ] Pembayaran breakdown Tunai vs Non-Tunai sesuai data
- [ ] Top 10 produk muncul sesuai data
- [ ] Date picker mengubah data dengan benar

## Settings
- [ ] Settings → Profil Toko → edit nama/alamat/telp → Simpan
- [ ] PPN toggle on/off → tax muncul/hilang di Checkout
- [ ] PPN persen ubah → calculate ulang Checkout
- [ ] Footer struk edit → Simpan → muncul di struk transaksi baru
- [ ] Ubah PIN: 3 step (PIN lama → PIN baru → konfirmasi)
- [ ] Tap "Kunci" → kembali ke PinLogin (data tetap)

## Backup & Restore
- [ ] Settings → Backup & Restore
- [ ] Export → SAF picker muncul, simpan ke folder pilihan
- [ ] File `.db` valid (cek di file manager)
- [ ] Import file `.db` valid → restore berhasil → restart app
- [ ] Import file non-`.db` (misal .jpg) → error "File backup tidak valid"

## Performance & Stability
- [ ] Tambah 100 produk dummy, scroll list lancar
- [ ] Rotate device di tengah Checkout, state tetap (qty stepper, paid, dst)
- [ ] Dark theme semua screen readable (Settings sistem → Dark)
- [ ] Tutup app, buka lagi: PIN login wajib (lockout state persist)

## Edge Cases
- [ ] Tambah produk dengan SKU duplicate → error dari Room unique constraint
- [ ] Hapus kategori yang masih punya produk → error dialog
- [ ] Reset PIN: belum diimplementasi di Tier A (catatan: harus uninstall ulang)
- [ ] Foto produk: belum diimplementasi di Tier A (toggle imageUri belum di UI)

## Issue Diketahui (Tier A scope)
- Foto produk via picker belum di-wire ke UI (schema sudah siap di Phase 1)
- Barcode scanner belum ada (kolom barcode sudah di schema sejak v1)
- Multi-user/role admin-kasir belum ada (Tier B)
- Bluetooth thermal printer belum ada (Tier B)
