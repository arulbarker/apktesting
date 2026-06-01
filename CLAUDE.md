# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

**Kasir Vapestore** is an offline Android POS (point-of-sale) app for an Indonesian vape boutique. Single-user, PIN-protected, Room SQLite storage. Tier A scope = setup wizard, PIN login, products with variants, POS/cart, checkout, PDF receipts, daily reports, backup/restore. User-facing copy is **Bahasa Indonesia** — match that tone when adding strings.

Application id / namespace: `com.vapestoreunik.madep`. Source root: `app/src/main/java/com/vapestoreunik/madep/`. The package name is historical; the brand name is "Kasir Vapestore".

## Common commands

All commands run from repo root via the bundled wrapper (`gradlew.bat` on Windows / `./gradlew` on POSIX):

```powershell
.\gradlew assembleDebug                       # build debug APK
.\gradlew installDebug                        # build + push to attached device
.\gradlew test                                # JVM unit tests (src/test) — fast, ~30 tests
.\gradlew connectedDebugAndroidTest           # instrumented tests (src/androidTest) — needs device/emulator
.\gradlew :app:testDebugUnitTest --tests "*CalculateTotalsUseCaseTest*"   # single test class
.\gradlew :app:testDebugUnitTest --tests "*CalculateTotalsUseCaseTest.tax half-up rounding"  # single test method
.\gradlew clean                               # nuke build/ + .gradle caches per module
.\gradlew --stop                              # kill all Gradle daemons (use after toolchain changes)
```

There is **no lint task wired up** beyond the default `lintDebug`. There is no separate format step — keep code style consistent with surrounding files.

`testInstrumentationRunner` in `app/build.gradle.kts:19` is set to `com.vapestoreunik.madep.HiltTestRunner`, but **that file doesn't exist yet**. Either add a `HiltTestRunner extends AndroidJUnitRunner` in `src/androidTest`, or remove the line, before running `connectedAndroidTest`. Plain Room DAO tests work because they don't need Hilt.

## Version compatibility (do not bump blindly)

`gradle/libs.versions.toml` is pinned to a verified-working combination. **This was hard-won** — earlier attempts with newer versions broke the build chain. The constraints:

- **Kotlin 2.0.21** + **KSP 2.0.21-1.0.28** — must match exactly (KSP versioning ties to Kotlin)
- **AGP 8.9.1** — minimum for Navigation 3 alpha10; do not upgrade past 8.x without retesting the full chain. Ignore Android Studio's "AGP Upgrade Assistant" notification.
- **Compose BOM 2024.12.01** + **Compose compiler plugin via Kotlin 2.0.21**
- **Navigation 3 alpha10** (`androidx.navigation3:*`) — different package from old `androidx.navigation` (Nav2). API is alpha; expect breaking changes if you bump.
- **Hilt 2.52** + KSP (not kapt)
- **Room 2.6.1** — `fallbackToDestructiveMigrationOnDowngrade()` takes no args on this version
- **JDK 17** target; `kotlin { jvmToolchain(17) }`

If a version bump is needed, change one library at a time and run `assembleDebug` + `test` before chaining.

## Architecture

### Layers

Clean architecture, flowing UI → ViewModel → Repository → DAO/DataStore:

```
ui/<feature>/         Composable screens + Hilt ViewModels (StateFlow)
core/ui/components/   Reusable branded composables (BrandHeader, MetricCard, PrimaryButton, etc.)
core/common/          Formatters, KasirException sealed hierarchy
domain/model/         Cart, CartItem, ReceiptData, etc. — pure Kotlin
domain/usecase/       CalculateTotalsUseCase, CheckoutUseCase, BuildReceiptPdfUseCase
data/repository/      Category/Product/Transaction/Auth + Transactor abstraction
data/local/           Room entities, DAOs, KasirDatabase
data/preferences/     DataStore (StoreProfile, TaxConfig, PIN hash, lockout state)
di/                   Hilt modules (Database, Preferences, Repository, Clock)
theme/                Color/Type/Shape/Theme — brand-locked, Material You disabled
```

### Navigation (Navigation 3, alpha10)

`Navigation.kt` builds a single `NavDisplay` over a `rememberNavBackStack`. `NavigationKeys.kt` declares one `@Serializable data object`/`data class` per destination. `RootViewModel` gates the initial key: `SetupWizard` if `prefs.setupCompleted == false`, else `PinLogin`.

Within `MainNavigation`, a local `fun clearAndPush(key: NavKey)` is defined inline rather than as an extension. **Do not extract it** — type inference for `NavBackStack<T>` extension functions is broken in this alpha and the inline form sidesteps it.

`MainScaffoldScreen` owns the bottom-nav tabs (Beranda/Kasir/Produk/Riwayat) and is its own destination. From it, push destinations are propagated up via callbacks (`onOpenSettings`, `onOpenCheckout`, etc.) so the back stack is owned by `MainNavigation`, not the scaffold.

### Money & rounding (critical invariant)

**All money is `Long` IDR (no decimals).** Never use `Double`/`Float`. `CalculateTotalsUseCase` uses integer half-up rounding:

```kotlin
private fun roundHalfUp(numerator: Long, denom: Long): Long =
    (numerator + denom / 2) / denom
```

Order of operations: `subtotal → discount → tax → total → change`. Discount is computed before tax. Negative inputs are coerced; discounts exceeding subtotal throw `DiscountExceedsSubtotal`. See `CalculateTotalsUseCaseTest.kt` for the rounding contract.

`RupiahFormatter` (in `core/common/`) uses `id_ID` locale with dot thousands-separator (`Rp 1.500.000`).

### Transaction atomicity & the Transactor abstraction

`CheckoutUseCase.execute()` writes 4+ tables in one Room transaction: re-check stock → insert transaction → insert items → adjust stock → insert stock log. To make this **unit-testable without an Android dependency**, the use case takes a `Transactor` interface (in `data/repository/Transactor.kt`).

- Production: `RoomTransactor` wraps `db.withTransaction { ... }`. Bound in `RepositoryModule`.
- Tests: `NoopTransactor` in `src/test` just invokes the block.
- Real Room rollback behavior is verified in `CheckoutAtomicityTest` (instrumented, in `src/androidTest`).

If you add another multi-table write path, follow the same pattern — inject `Transactor`, write the integration test on `androidTest`, unit-test the logic with the noop.

### Snapshot fields in `transaction_items`

`TransactionItemEntity` stores `productNameSnapshot`, `variantNameSnapshot`, `priceSnapshot` (Long) **alongside** `variantId`. The snapshots are the source of truth for historical receipts — never re-resolve them by joining to current `variants`/`products` when rendering. Product can be renamed, deleted, or repriced; old receipts must still show what the customer actually paid.

### `ClockModule` (don't inline it back)

`AuthRepository` and `CheckoutUseCase` accept `clock: () -> Long` so tests can advance time. Hilt **cannot** inject a function type via a Kotlin default parameter — the default is silently ignored at runtime. `di/ClockModule.kt` provides `() -> Long = { System.currentTimeMillis() }`. Keep it.

### `CartHolder` bridge

`PosScreen` and `CheckoutScreen` are separate navigation destinations, so they can't share a ViewModel. `CartHolder` (`@Singleton`) holds the in-flight cart between them and is reset after successful checkout. Don't replace it with savedStateHandle / serializing the cart across nav args — items reference Room IDs and the cart is short-lived.

### PIN auth

`DefaultAuthRepository` uses bcrypt (cost 10) for PIN hashing. 5 failed attempts trigger a 30-second lockout; constants live on the class companion. Lockout state persists in DataStore (`auth.failedAttempts`, `auth.lockoutUntil`) so it survives process death. The clock is injected for testability.

### Theme / brand lock

Brand identity is **Onyx + Gold** (premium vape boutique). Material You **dynamic color is intentionally disabled** in `KasirVapestoreTheme` so brand colors stay consistent across all phones. Full palette + recipes in `design-system/MASTER.md`.

Material 3 slot mapping to remember:
- `primary` = onyx-900 (`#1C1917`) — app bars, headings, body text
- `tertiary` = gold-600 (`#CA8A04`) — **all primary CTAs** (Bayar, Selesaikan, Simpan). `onTertiary` = onyx-950, giving black-on-gold contrast.
- `secondary` = gold-500 — accents, chips, secondary highlights

Two elements are **brand-locked** (theme-independent, use direct `Brand*` tokens, not Material slots) and should stay that way:
- `MetricCard.Primary` (omzet hero) — onyx gradient + gold value + royal-gold accent bar
- `BrandHeader` monogram circle — onyx gradient with gold ring + gold "K"

When adding screens, reach for components from `core/ui/components/` (`MetricCard`, `ProductCard`, `SectionHeader`, `PrimaryButton`, `SecondaryButton`, `BrandHeader`) before falling back to raw Material 3 — they encode the brand spacing/typography/elevation choices. Use `tertiary` (gold) — not `primary` (onyx) — for product price labels: it's a deliberate retail-psychology choice.

Typography uses Google Fonts at runtime (Rubik for display/headings, Inter for body). The `font_certs.xml` resource holds the GMS provider certs needed for downloadable fonts.

## KasirException — error language

Domain errors are a sealed hierarchy in `core/common/KasirException.kt` with **Bahasa Indonesia messages**. When adding a new business-rule error, extend `KasirException` rather than throwing a generic `IllegalStateException` — it makes the message reusable in UI surfaces and the type checkable in `when` branches.

## Testing strategy

- `src/test/` — JVM unit tests: formatters, use cases, repositories with fakes (`FakeAppPreferences`, `FakeVariantDao`, `FakeTransactionDao`, `FakeTransactionRepository`, `FakeStockLogDao`, `NoopTransactor`).
- `src/androidTest/` — Room DAO tests + checkout atomicity test. Need a device/emulator.
- Use **Turbine** (`app.cash.turbine`) for StateFlow assertions in tests.
- Tests use `1780272000000L` (2026-06-01 UTC) for fixed timestamps — match this when adding time-sensitive tests, don't drift to a different year.

## File layout convention

One screen = one file. ViewModels live next to their screens (`PosScreen.kt` + `PosViewModel.kt`). State classes are nested inside the ViewModel file or declared at top level in the same package. Avoid creating `state/` subdirectories.

## Spec / plan references

- `docs/superpowers/specs/2026-06-01-kasir-vapestore-design.md` — Tier A spec (entities, flows, edge cases)
- `docs/superpowers/plans/2026-06-01-kasir-vapestore-tier-a.md` — phased implementation plan
- `docs/manual-test-checklist.md` — manual QA list before release
- `design-system/MASTER.md` — brand design system source of truth

Read these before architecting new features — Tier B items (multi-user, Bluetooth printer, customer DB, shifts) are listed there as **deferred**, not "to do soon".
