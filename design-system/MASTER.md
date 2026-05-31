# Kasir Vapestore — Design System Master

> **Source of truth** untuk semua design decisions. Page-specific overrides bisa ditaruh di `pages/<name>.md`.

## Brand Identity

**Tagline (internal)**: "Premium vape boutique experience for cashier"

**Positioning**: Bukan POS generik — ini specifically untuk toko vape modern di Indonesia. Vibe: premium, edgy, terpercaya. Bukan murahan, bukan klinik, bukan supermarket.

**Personality**: bold, modern, geometric, sedikit nightlife/edgy tapi tetap profesional.

## Color Palette

### Light Mode (default — cashier daytime use)
```
Primary       #0F172A  slate-900    app bar, headings, primary text
PrimaryContainer #E2E8F0 slate-200   subtle highlight container
Secondary     #7C3AED  purple-600   accent untuk vape-specific elements
SecondaryContainer #EDE9FE purple-100
Tertiary (CTA) #F59E0B amber-500    PRIMARY CTA color (Bayar, Selesaikan)
TertiaryContainer #FEF3C7 amber-100  CTA card backgrounds
Background    #FAFAF9  stone-50     warm off-white main bg
Surface       #FFFFFF              card surface
SurfaceVariant #F5F5F4  stone-100   subtle card variant
Error         #DC2626  red-600
Success       #16A34A  green-600    (custom — bukan Material default)
OnPrimary     #FFFFFF
OnSurface     #0F172A
OnSurfaceVariant #57534E stone-600
```

### Dark Mode (optional — beberapa user prefer)
```
Primary       #F1F5F9  slate-100    inverted
PrimaryContainer #1E293B slate-800
Secondary     #A78BFA  purple-400
Tertiary (CTA) #FBBF24 amber-400   slightly brighter for dark bg
Background    #0C0A09  stone-950    premium black
Surface       #1C1917  stone-900
SurfaceVariant #292524 stone-800
Error         #F87171  red-400
OnPrimary     #0F172A
OnSurface     #FAFAF9
OnSurfaceVariant #A8A29E stone-400
```

## Typography (Google Fonts)

| Style | Font | Weight | Size | Usage |
|---|---|---|---|---|
| displayLarge | Rubik | 700 | 36sp | Setup wizard hero |
| displayMedium | Rubik | 700 | 28sp | Section banners |
| headlineLarge | Rubik | 700 | 24sp | Screen titles |
| headlineMedium | Rubik | 600 | 22sp | Card headers |
| titleLarge | Rubik | 600 | 20sp | TopAppBar title |
| titleMedium | Rubik | 600 | 16sp | List item primary |
| titleSmall | Rubik | 500 | 14sp | Subheaders |
| bodyLarge | Inter | 400 | 16sp | Body text default |
| bodyMedium | Inter | 400 | 14sp | Secondary body |
| bodySmall | Inter | 400 | 12sp | Caption, metadata |
| labelLarge | Inter | 600 | 14sp | Button labels |
| labelMedium | Inter | 500 | 12sp | Chip labels |

## Shape Scale

```
ExtraSmall  4dp   — chips, badges
Small       8dp   — small buttons, input fields
Medium      12dp  — cards (default)
Large       16dp  — large CTA buttons, dialogs
ExtraLarge  24dp  — bottom sheets, modals, hero containers
```

## Elevation (Material 3 inspired)

```
Level 0: 0dp   — flat surfaces
Level 1: 1dp   — subtle cards
Level 2: 3dp   — raised cards, app bar scrolled
Level 3: 6dp   — FAB, important cards
Level 4: 8dp   — modal sheets, navigation drawer
Level 5: 12dp  — top-level (rarely used)
```

## Spacing

8dp grid system:
```
xs   4dp
sm   8dp
md   16dp  — default content padding
lg   24dp
xl   32dp
xxl  48dp  — section gaps
```

## Branding Elements

### App Logo Concept
- **Wordmark**: "KASIR" (Rubik Bold 28sp slate-900) + "VAPESTORE" (Rubik Medium 12sp purple-600 letter-spacing 2sp)
- **Monogram**: "K" dalam circle dengan gradient slate-900 → purple-600 (untuk icon launcher / hero)
- **Tagline ringan**: "POS Premium Vape" (Inter Regular 14sp stone-600)

### Brand Touchpoints
1. **App Launcher Icon**: K monogram dengan vapor swirl detail (TBD via designer)
2. **Splash Screen**: gradient slate-900 → purple-700 dengan K monogram center
3. **Setup Wizard Header**: large hero dengan brand name + tagline
4. **PinLogin**: brand name above PIN dots
5. **MainScaffold TopAppBar**: subtle wordmark left, gear right

## UX Principles for POS Context

1. **Speed**: tap target minimal 48x48dp (lebih besar dari Material standard 44dp) karena kasir buru-buru
2. **Visibility**: contrast 7:1 untuk teks penting (lebih ketat dari WCAG AA 4.5:1)
3. **Forgiving**: aksi destructive (hapus item, batal transaksi) wajib double-confirm
4. **Idempotency**: tombol "Selesaikan Transaksi" auto-disable saat processing (sudah ada)
5. **Branding subtle, tidak intrusif**: brand hadir di splash + setup + login, tapi screen kasir harian fokus ke fungsi

## Anti-patterns (jangan lakukan)

- ❌ Emoji sebagai icon (pakai Material Icons SVG)
- ❌ Hard shadows / harsh borders di card
- ❌ Lebih dari 3 CTA primary di 1 screen
- ❌ Warna saturasi tinggi di area konten utama (cashier eye strain)
- ❌ Animasi > 300ms untuk transisi UI (kasir butuh kecepatan)
- ❌ Card tanpa rounded corners (mood "boutique" hilang)

## Implementation Checklist

- [ ] Update `theme/Color.kt` dengan palette baru
- [ ] Update `theme/Type.kt` dengan Rubik + Inter via GoogleFont
- [ ] Update `theme/Theme.kt` dengan light + dark scheme proper
- [ ] Tambah `theme/Shape.kt` (Material 3 ShapeScheme custom)
- [ ] Add dependency: `androidx.compose.ui:ui-text-google-fonts`
- [ ] Buat `core/ui/components/BrandHeader.kt` (wordmark logo)
- [ ] Polish SetupWizard dengan brand hero
- [ ] Polish PinLogin dengan brand wordmark
- [ ] Polish MainScaffold TopAppBar dengan branding
- [ ] Polish Dashboard cards dengan gradient subtle + better hierarchy
- [ ] Polish PosScreen product cards dengan elevation
- [ ] Polish Checkout summary dengan brand CTA color (amber)
- [ ] Polish Receipt dengan branded header layout
