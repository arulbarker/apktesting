# Kasir Vapestore — Design Spec (Tier A)

| Meta | Value |
|---|---|
| **Tanggal** | 2026-06-01 |
| **Project** | KasirVapestore (`com.vapestoreunik.madep`) |
| **Tier** | A — Core POS |
| **Platform** | Android native (Kotlin + Jetpack Compose) |
| **Mode** | Offline-only (Room) |
| **Status** | Approved by user (sections 1-5) |

---

## 1. Tujuan & Non-Goals

### Tujuan
Membangun aplikasi kasir Android **offline** untuk toko Vapestore Unik dengan fitur inti yang cukup untuk operasional harian: manajemen produk + varian + stok, checkout dengan keranjang, struk PDF yang bisa di-share, dan laporan penjualan harian. Tidak butuh internet — semua data tersimpan lokal di Room.

### Non-Goals (Tier A)
Berikut sengaja **TIDAK** dikerjakan di Tier A (ditunda ke Tier B/C atau ditolak total):

- Multi-user / pemisahan role admin vs kasir → Tier B
- Bluetooth thermal printer (58mm ESC/POS) → Tier B
- Barcode scanner (kamera/eksternal) → Tier C
- Customer / member database → Tier B
- Shift kasir (buka/tutup kas) → Tier B
- Hutang/piutang & retur → Tier C
- Multi-outlet (data terpisah per outlet) → Tier C
- Cloud sync / SaaS / backend → ditolak total (offline-only)
- iOS / cross-platform → ditolak total (Android-only)
- Diskon per-item (Tier A hanya diskon per-transaksi) → Tier B
- Split payment (combo metode) → Tier B
- Print barcode label produk → Tier C

---

## 2. Scope Tier A

Fitur inti yang **akan** dibangun:

1. **Setup wizard** sekali pakai (profil toko + PIN)
2. **PIN login** angka 4-6 digit dengan lockout 5x salah → 30 detik
3. **Manajemen produk** dengan pattern Product + Variant (default variant untuk produk tanpa varian)
4. **Manajemen kategori** (seed: Liquid, Device/Mod, Coil & Atomizer, Cotton, Baterai, Aksesoris)
5. **Stok per-varian** dengan riwayat mutasi (`stock_logs`) sejak v1
6. **POS / Keranjang** dengan grid produk + filter kategori + search
7. **Checkout** dengan diskon (% atau nominal) + PPN configurable + metode bayar Tunai/Non-Tunai
8. **Struk** sebagai PDF yang bisa di-share atau di-print via Android Print Framework
9. **Riwayat transaksi** dengan filter rentang tanggal + payment method
10. **Laporan harian**: omzet, jumlah transaksi, top 10 produk, breakdown tunai vs non-tunai
11. **Backup & restore** database via Storage Access Framework
12. **Pengaturan**: profil toko, PPN, footer struk, ubah PIN, kelola kategori, kunci app

---

## 3. Arsitektur

### Layer
```
┌─────────────────────────────────────────────────────┐
│ UI (Compose Screens + ViewModels)                    │
│   ↑ StateFlow<UiState>                               │
├─────────────────────────────────────────────────────┤
│ Domain (model murni + UseCase opsional)              │
│   panggil bila business logic non-trivial            │
├─────────────────────────────────────────────────────┤
│ Data (Repository → DAO / DataStore)                  │
│   ↑ Flow<List<Entity>> reactive                      │
├─────────────────────────────────────────────────────┤
│ Local Storage: Room (relational) + DataStore (kv)    │
└─────────────────────────────────────────────────────┘
```

Aturan:
- UI mengakses ViewModel saja
- ViewModel mengakses Repository (UseCase **hanya** kalau logic non-trivial)
- Repository mengakses DAO/DataStore (1 repo bisa pakai multiple DAO)

### Struktur folder
```
app/src/main/java/com/vapestoreunik/madep/
├── KasirVapestoreApp.kt           # @HiltAndroidApp
├── MainActivity.kt                 # @AndroidEntryPoint
├── Navigation.kt                   # NavDisplay + entry per screen
├── NavigationKeys.kt               # @Serializable NavKey objects
│
├── core/
│   ├── common/                     # Result, formatters, Constants, KasirException
│   ├── ui/components/              # NumPad, QtyStepper, EmptyState, dst
│   └── theme/                      # Color, Theme, Type
│
├── data/
│   ├── local/
│   │   ├── KasirDatabase.kt
│   │   ├── dao/                    # ProductDao, VariantDao, TransactionDao, CategoryDao, StockLogDao
│   │   └── entity/                 # *Entity classes
│   ├── preferences/
│   │   └── AppPreferences.kt       # DataStore wrapper
│   └── repository/
│       ├── ProductRepository.kt
│       ├── TransactionRepository.kt
│       ├── CategoryRepository.kt
│       └── AuthRepository.kt
│
├── domain/
│   ├── model/                      # Cart, CartItem, TransactionResult, ReceiptData
│   └── usecase/
│       ├── CheckoutUseCase.kt
│       ├── CalculateTotalsUseCase.kt
│       └── BuildReceiptPdfUseCase.kt
│
├── di/
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── PreferencesModule.kt
│
└── ui/
    ├── setup/                      # SetupWizardScreen
    ├── auth/                       # PinLoginScreen, PinSetScreen
    ├── dashboard/                  # DashboardScreen
    ├── pos/                        # PosScreen, CheckoutScreen
    ├── products/                   # ProductListScreen, ProductFormScreen
    ├── categories/                 # CategoryListScreen
    ├── transactions/               # TransactionHistoryScreen, ReceiptDetailScreen
    ├── reports/                    # ReportsScreen
    └── settings/                   # SettingsScreen + sub-screens
```

### Module
- **Single Android module** (`:app`). Tidak modularize di Tier A.
- Gradle version catalog (`libs.versions.toml`) — sudah ada.
- `minSdk = 24`, `targetSdk = 36`, `compileSdk = 36`.
- Compose BOM (sudah ada).

### Dependencies tambahan
| Library | Tujuan |
|---|---|
| `androidx.room:room-runtime`, `room-ktx`, `room-compiler` (KSP) | Database lokal |
| `com.google.dagger:hilt-android`, `hilt-compiler` (KSP), `androidx.hilt:hilt-navigation-compose` | DI |
| `androidx.datastore:datastore-preferences` | Key-value preferences |
| `io.coil-kt.coil3:coil-compose` | Foto produk |
| `androidx.compose.material:material-icons-extended` | Icon set |
| `at.favre.lib:bcrypt` | Hash PIN |
| `com.google.devtools.ksp` plugin | Code gen (Room + Hilt) |
| `app.cash.turbine:turbine` (test) | Flow testing |
| `androidx.room:room-testing` (androidTest) | DAO + migration test |

---

## 4. Data Model (Room schema v1)

### ERD Overview

```
categories ──< products ──< product_variants
                                  ▲
                                  │
transactions ──< transaction_items┘
                                  │
                          stock_logs
```

### Tabel `categories`
| Field | Type | Catatan |
|---|---|---|
| `id` | Long PK auto | |
| `name` | String NOT NULL | |
| `icon` | String? | nama icon Material |
| `sortOrder` | Int | urutan display |
| `createdAt` | Long | epoch millis |

**Seed awal saat setup**: Liquid, Device/Mod, Coil & Atomizer, Cotton, Baterai, Aksesoris.

### Tabel `products`
| Field | Type | Catatan |
|---|---|---|
| `id` | Long PK auto | |
| `name` | String NOT NULL | |
| `categoryId` | Long FK → categories | `ON DELETE RESTRICT`, indexed |
| `brand` | String? | |
| `imageUri` | String? | URI dari Storage Access Framework |
| `description` | String? | |
| `hasVariants` | Boolean | `false` → ada 1 default variant tersembunyi |
| `isActive` | Boolean | soft delete |
| `createdAt`, `updatedAt` | Long | |

### Tabel `product_variants`
| Field | Type | Catatan |
|---|---|---|
| `id` | Long PK auto | |
| `productId` | Long FK → products | `ON DELETE CASCADE`, indexed |
| `name` | String NOT NULL | "30ml / 3mg" — kosong "" untuk default variant |
| `sku` | String? UNIQUE | optional, indexed unique |
| `barcode` | String? UNIQUE | sejak v1, dipakai di Tier C |
| `price` | Long | IDR utuh, e.g. `75000` |
| `cost` | Long? | HPP optional |
| `stock` | Int | denormalized, source of truth |
| `lowStockThreshold` | Int | default 5 |
| `isActive` | Boolean | |
| `createdAt`, `updatedAt` | Long | |

**Pattern default variant**: Produk dengan `hasVariants=false` memiliki **1 variant dengan name=""**. UI menyembunyikan picker varian, langsung pakai variant ini. Toggle `hasVariants` ke true: default variant tetap ada (di-rename ke "Default" di UI). Sama dengan pattern Shopify/Square.

### Tabel `transactions`
| Field | Type | Catatan |
|---|---|---|
| `id` | Long PK auto | |
| `code` | String UNIQUE NOT NULL | format `TRX-YYYYMMDD-NNNN` |
| `subtotal` | Long | sebelum diskon/pajak |
| `discountType` | String? | `"PERCENT"` / `"NOMINAL"` / `null` |
| `discountValue` | Long | nilai input (% atau nominal) |
| `discountAmount` | Long | nominal yang dikurangi (computed) |
| `taxPercent` | Int | dari settings global, snapshot saat checkout |
| `taxAmount` | Long | computed |
| `total` | Long | final |
| `paid` | Long | jumlah dibayar pelanggan |
| `change` | Long | `paid - total` |
| `paymentMethod` | String | `"TUNAI"` / `"NON_TUNAI"` |
| `note` | String? | |
| `createdAt` | Long | indexed (untuk laporan harian/rentang) |

### Tabel `transaction_items`
| Field | Type | Catatan |
|---|---|---|
| `id` | Long PK auto | |
| `transactionId` | Long FK → transactions | `ON DELETE CASCADE`, indexed |
| `variantId` | Long FK → variants | `ON DELETE RESTRICT`, indexed |
| `productNameSnapshot` | String NOT NULL | supaya struk lama tetap akurat |
| `variantNameSnapshot` | String NOT NULL | |
| `priceSnapshot` | Long NOT NULL | |
| `qty` | Int NOT NULL | |
| `subtotal` | Long | `priceSnapshot × qty` |

### Tabel `stock_logs`
| Field | Type | Catatan |
|---|---|---|
| `id` | Long PK auto | |
| `variantId` | Long FK → variants | indexed |
| `type` | String NOT NULL | `IN` / `OUT` / `ADJUSTMENT` / `SALE` / `INIT` |
| `qty` | Int NOT NULL | signed: + untuk IN, − untuk OUT/SALE |
| `balanceAfter` | Int NOT NULL | stok setelah mutasi |
| `refType` | String? | `TRANSACTION` / `MANUAL` / `INIT` |
| `refId` | Long? | id transaction (kalau SALE) |
| `note` | String? | |
| `createdAt` | Long | indexed |

### DataStore (preferences)
```
- store.name: String
- store.address: String
- store.phone: String
- store.logoUri: String?
- auth.pinHash: String          # bcrypt hash
- auth.lastUnlockedAt: Long
- auth.failedAttempts: Int      # untuk lockout
- auth.lockoutUntil: Long       # epoch millis, 0 = tidak terkunci
- setup.completed: Boolean
- tax.enabled: Boolean
- tax.percent: Int              # 0-100
- receipt.footer: String        # default: "Terima kasih sudah berbelanja!"
```

### Konvensi
| Aspek | Konvensi |
|---|---|
| Uang | `Long` IDR utuh (tidak ada sen) |
| Timestamp | `Long` epoch millis (format di UI layer) |
| ID | Auto-increment `Long` |
| Soft delete | `isActive: Boolean` untuk products/variants |
| Money calculation | Integer math + round-half-up eksplisit |
| Migration strategy | Manual migration sejak v1 (bukan `@AutoMigration`) |

---

## 5. Screens & Flow

### Navigation keys
```kotlin
@Serializable data object SetupWizard : NavKey
@Serializable data object PinLogin : NavKey
@Serializable data object MainScaffold : NavKey
@Serializable data object Checkout : NavKey
@Serializable data class Receipt(val transactionId: Long) : NavKey
@Serializable data class ProductForm(val productId: Long? = null) : NavKey
@Serializable data object CategoryManage : NavKey
@Serializable data object Reports : NavKey
@Serializable data object Settings : NavKey
@Serializable data object ChangePin : NavKey
@Serializable data object BackupRestore : NavKey
@Serializable data object StoreProfile : NavKey
```

### Alur global
```
App start
   │
   ▼
{setup.completed?}
   ├── false ──► SetupWizard (3 step) ──► setup.completed=true ──► PinLogin
   └── true  ──► PinLogin
                    │ PIN valid
                    ▼
                MainScaffold (4 tab + gear icon)
                  ├── [Beranda]
                  ├── [Kasir] ─► Checkout ─► Receipt ─► "Transaksi Baru"
                  ├── [Produk] ─► ProductForm (tambah/edit)
                  ├── [Riwayat] ─► Receipt (read-only)
                  └── [⚙] ─► Settings
                              ├── StoreProfile
                              ├── ChangePin
                              ├── CategoryManage
                              ├── BackupRestore
                              └── Reports
```

### Daftar screen

| # | Screen | Tujuan |
|---|---|---|
| 1 | `SetupWizardScreen` | First-launch: profil toko + PIN |
| 2 | `PinLoginScreen` | Verifikasi PIN, lockout 5 salah → 30s |
| 3 | `MainScaffoldScreen` | Host bottom nav 4 tab + TopAppBar |
| 4 | `DashboardScreen` (Beranda) | Ringkasan hari ini, stok rendah, top 5 |
| 5 | `PosScreen` (Kasir) | Grid produk + keranjang split/sheet |
| 6 | `CheckoutScreen` | Diskon + pajak + payment + tombol selesai |
| 7 | `ReceiptScreen` | Struk view, share PDF, print, transaksi baru |
| 8 | `ProductListScreen` (Produk) | Search + filter kategori + grid |
| 9 | `ProductFormScreen` | Tambah/edit + varian inline. Stok awal default 0 (QtyStepper). Foto: pick → resize 512×512 → simpan URI SAF |
| 10 | `TransactionHistoryScreen` (Riwayat) | List dengan filter tanggal & payment |
| 11 | `SettingsScreen` | List item ke sub-setting |
| 12 | `StoreProfileScreen` | Edit profil toko |
| 13 | `ChangePinScreen` | PIN lama → PIN baru |
| 14 | `CategoryManageScreen` | CRUD kategori |
| 15 | `BackupRestoreScreen` | Export DB + Import DB |
| 16 | `ReportsScreen` | Laporan harian (Tier A) |

### Layout PosScreen
- **Tablet / landscape**: split layout — kiri grid produk dengan search + chip kategori, kanan keranjang dengan qty stepper + tombol Bayar.
- **Phone / portrait**: grid produk full screen, keranjang sebagai badge FAB → tap → bottom sheet keranjang.

Tap produk:
- `hasVariants=false` → langsung +1 ke cart
- `hasVariants=true` → bottom sheet pilih varian (radio list)

Stok 0 → item disabled + label "Habis".

### Alur Checkout
1. User input diskon (opsional) dan pilih metode bayar
2. Untuk Tunai: input jumlah bayar → kembalian dihitung otomatis
3. Validasi: `paid >= total`, `discount <= subtotal`, cart tidak kosong
4. Tap "Selesaikan Transaksi" → `CheckoutUseCase.execute(cart, payment)`:
   1. Buka Room `@Transaction` block
   2. Re-check stok setiap item — abort `StockInsufficient` kalau kurang
   3. Generate code `TRX-YYYYMMDD-NNNN` (sequence per hari via COUNT)
   4. Insert `transactions` row
   5. Insert semua `transaction_items` dengan snapshot fields dari variant
   6. Update `variant.stock = stock - qty` untuk setiap item
   7. Insert `stock_logs` row per item (type=SALE, refType=TRANSACTION, refId=tx.id)
   8. Commit
5. Sukses → navigate ke `Receipt(transactionId)`
6. Gagal → snackbar error, cart tetap utuh

### Komponen reusable (`core/ui/components/`)
- `NumPad` — PIN screens
- `QtyStepper` — `[-] [n] [+]`
- `RupiahTextField` — auto-format `Rp 1.234`
- `EmptyState`
- `LoadingIndicator`
- `ConfirmDialog`
- `CategoryChip`

### State pattern setiap screen
```kotlin
sealed interface XxxUiState {
    data object Loading : XxxUiState
    data class Ready(val data: ...) : XxxUiState
    data class Error(val message: String) : XxxUiState
}
```
ViewModel expose `StateFlow<XxxUiState>`, UI `collectAsStateWithLifecycle()`.

---

## 6. Error Handling

### Domain exception (`core/common/KasirException.kt`)
```kotlin
sealed class KasirException(message: String) : Exception(message) {
    class StockInsufficient(val variantName: String, val available: Int, val requested: Int) : KasirException(...)
    class InvalidPin(val attemptsLeft: Int) : KasirException("PIN salah")
    class PinLockedOut(val remainingSeconds: Int) : KasirException(...)
    class DuplicateBarcode(val sku: String) : KasirException(...)
    class CategoryHasProducts(val name: String) : KasirException(...)
    class ProductHasTransactions(val name: String) : KasirException(...)
    class BackupFileInvalid : KasirException(...)
    class BackupSchemaIncompatible : KasirException(...)
    class StorageWriteFailed(cause: Throwable) : KasirException(...)
    class CartEmpty : KasirException(...)
    class PaymentInsufficient(val total: Long, val paid: Long) : KasirException(...)
    class DiscountExceedsSubtotal : KasirException(...)
}
```

### Pola ViewModel
```kotlin
useCase.execute(...)
  .onSuccess { _uiState.value = UiState.Success(it) }
  .onFailure { e ->
      Log.e(TAG, "operation failed", e)
      _uiState.value = UiState.Error(messageFor(e))
  }
```

### Mapping exception → user message (Bahasa Indonesia, di `strings.xml`)

| Exception | Pesan | UI |
|---|---|---|
| `StockInsufficient` | "Stok {nama} tidak cukup (tersisa X)" | Snackbar dengan action "Lihat" |
| `PaymentInsufficient` | (dicegah, tombol disabled) | — |
| `CartEmpty` | (dicegah, tombol disabled) | — |
| `InvalidPin(n)` | "PIN salah. Sisa X percobaan" | Shake + clear input |
| `PinLockedOut(s)` | "Terkunci $s detik" | NumPad disabled, countdown |
| `DuplicateBarcode` | "Barcode {sku} sudah dipakai" | Field error inline |
| `BackupFileInvalid` | "File backup tidak valid" | Dialog |
| `BackupSchemaIncompatible` | "Versi backup tidak kompatibel, update aplikasi" | Dialog blocking |
| `ProductHasTransactions` | "Produk punya riwayat. Nonaktifkan saja?" | Dialog dengan opsi soft-disable |
| `IOException` generic | "Gagal akses penyimpanan" | Snackbar |
| `OutOfMemoryError` | "Foto terlalu besar, pilih yang lebih kecil" | Toast |

### Logging
- `android.util.Log` saja di Tier A
- TAG = nama class
- Format: `Log.e(TAG, "operation failed", throwable)`
- Tidak boleh log data sensitif (PIN hash, customer data nanti)
- Release: aktifkan `isMinifyEnabled = true` + ProGuard strip log

---

## 7. Testing Strategy

### Piramida tes
- Unit (`src/test/`): ~30-50 tes
- Integration (`src/androidTest/`): ~5-10 tes (Room DAO + Repository)
- UI (`src/androidTest/`): 3-5 tes (happy path saja)

### Unit test wajib

**`CalculateTotalsUseCaseTest`** (PALING penting — uang)
- `subtotal sum items correctly`
- `discount percent 10 from 12345 returns 1235 (round-half-up)`
- `discount nominal cannot exceed subtotal throws DiscountExceedsSubtotal`
- `tax 11 percent applied after discount (order matters)`
- `tax disabled returns zero tax`
- `total = subtotal - discount + tax`
- `change = paid - total`
- `paid less than total throws PaymentInsufficient`
- `empty cart throws CartEmpty`

**`CheckoutUseCaseTest`** (logic, pakai `FakeTransactionRepository` + `FakeVariantDao` — cepat, di `src/test/`)
- `successful checkout decrements stock for all items`
- `successful checkout inserts stock_log per item`
- `successful checkout generates code TRX-YYYYMMDD-NNNN`
- `code sequence resets per day`
- `code sequence increments within same day`
- `stock insufficient throws StockInsufficient (logic check)`
- `snapshot fields copied from variant at checkout time, not referencing live data`

**Test atomicity asli dipisah ke integration test** — lihat `CheckoutAtomicityTest` di section integration tests di bawah (pakai in-memory Room beneran di `src/androidTest/`).

**`RupiahFormatterTest`**
- `format 0 returns Rp 0`
- `format 1000 returns Rp 1.000` (titik separator)
- `format 1234567 returns Rp 1.234.567`
- `parse Rp 1.234.567 returns 1234567L`
- `parse with whitespace and prefix tolerant`

**`PinAuthRepositoryTest`**
- `set pin stores bcrypt hash not plaintext`
- `verify correct pin returns success`
- `verify wrong pin returns failure with attempts left`
- `5 failed attempts triggers lockout`
- `lockout expires after 30 seconds`

**ViewModel tests** — pakai `FakeRepository` (hand-rolled, no MockK)
- `DashboardViewModelTest`
- `PosViewModelTest`
- `CheckoutViewModelTest`

### Integration tests

**`ProductDaoTest`**
- Insert produk + varian, query balik
- Foreign key constraint enforced (variant tanpa product → error)
- Query produk by kategori benar

**`TransactionDaoTest`**
- Atomic transaction: kalau salah satu insert gagal, semua rollback
- Cascade delete: hapus transaction → transaction_items ikut hapus
- Query laporan harian (rentang `createdAt`) return data benar

**`CheckoutAtomicityTest`** (pakai in-memory Room di `src/androidTest/`)
- `partial failure mid-transaction rolls back stock and transaction together` — simulasi: variant 2nd item dihapus di tengah-tengah, transaksi pertama tidak boleh ter-insert separuh
- `concurrent checkout same variant respects stock` — 2 coroutine launch parallel, hanya 1 yang sukses kalau stok cukup untuk 1
- `code sequence atomic` — tidak ada duplicate code dalam concurrent insert

**`MigrationTest`**
- Tier A start di v1. Begitu ada v2 nanti, test migration v1 → v2 wajib ada sebelum merge.

### UI tests (Compose, happy path saja)

- `OnboardingFlowTest`: install fresh → setup wizard → set PIN → login → masuk dashboard
- `SellingFlowTest`: dashboard → tab Kasir → tap produk → checkout tunai → receipt muncul, transaksi tercatat di riwayat
- `StockGuardTest`: produk stok 0 → tidak bisa tambah ke cart

### Manual test checklist (`docs/manual-test-checklist.md`)
- First launch flow (uninstall → reinstall)
- PIN lockout 5 salah → tunggu 30s → bisa lagi
- Backup file → uninstall → reinstall → restore → data utuh
- Tambah 100 produk, scroll list lancar
- Foto produk 5MB tidak crash
- Tap "Bayar" 2x cepat tidak buat 2 transaksi (idempotency UI guard)
- Rotate device di tengah checkout, state tidak hilang
- Dark theme semua screen readable

---

## 8. Appendix

### A. Konvensi kode
- Bahasa UI: **Bahasa Indonesia**, semua string di `strings.xml`
- Format uang: `NumberFormat.getInstance(Locale("id", "ID"))` dengan prefix "Rp "
- Format tanggal: `dd MMM yyyy` (e.g. "01 Jun 2026") untuk display, `yyyy-MM-dd` untuk format internal
- Theme: Material 3 + dynamic color (Android 12+), dark mode default mengikuti sistem
- Compose: `safeDrawingPadding` di root sudah ada — biarkan

### B. Library yang DITOLAK untuk Tier A
| Library | Alasan |
|---|---|
| Koin | Pakai Hilt (standard Google) |
| SQLDelight | Pakai Room |
| Compose Navigation klasik | Project sudah pakai Navigation 3 |
| Arrow / kotlin-result | `kotlin.Result` cukup |
| MockK / Mockito | Pakai hand-rolled fakes |
| Crashlytics / Sentry | Skip cloud deps di Tier A |
| Firebase Analytics | Skip cloud deps |
| Glide / Picasso | Pakai Coil 3 |
| Charting library (MPAndroidChart, dst) | Tier A laporan hanya angka, no chart |

### C. Setelah Tier A selesai
Fitur kandidat Tier B (urutan kasar):
1. Multi-user + role admin/kasir
2. Shift kasir
3. Bluetooth thermal printer
4. Customer/member database
5. Diskon per-item, voucher
6. Laporan mingguan/bulanan + chart
7. Stok masuk/keluar form proper (bulk import dari CSV)

Tidak ada commitment ke Tier B sebelum Tier A dipakai nyata dan dapat feedback.

### D. Keputusan implementasi (sudah lock)
1. **Nomor urut transaksi**: format `TRX-YYYYMMDD-NNNN`, sequence **reset per hari** (lebih readable: NNNN = transaksi ke-N di hari itu).
2. **Stok awal saat tambah produk**: default **0**, user input via QtyStepper di form. Tidak wajib > 0.
3. **Backup encryption**: **tidak ada encryption** di Tier A — file `.db` plain. User bertanggung jawab atas storage backup. Bisa ditambah Tier B kalau ada concern privacy.
4. **Logo toko**: format **PNG/JPG**, max **2MB**, resize ke **512×512** saat disimpan (pakai Coil transformation). Path disimpan sebagai URI Storage Access Framework.
5. **Footer struk default**: `"Terima kasih sudah berbelanja!\nTukar/kembali dalam 7 hari dengan struk."` — bisa diubah user via Settings.

### E. Pertanyaan terbuka (genuinely undecided)
Tidak ada — semua keputusan sudah locked. Spec siap untuk implementation plan.

---

## 9. Approval

Spec ini sudah dibahas section-by-section dan diapprove user pada 2026-06-01:
- ✅ Section 1: Ringkasan keputusan & default profesional
- ✅ Section 2: Arsitektur & folder structure
- ✅ Section 3: Data model (+ tambahan `barcode` di `product_variants`)
- ✅ Section 4: Screens & flow
- ✅ Section 5: Error handling & testing

**Next step**: Buat implementation plan via `superpowers:writing-plans` skill.
