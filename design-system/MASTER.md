# Kasir Vapestore — Brand Master

> **Source of truth** untuk semua design decisions. Page-specific overrides di `pages/<name>.md`.

## Brand Identity

**Tagline (internal)**: "Black on gold — premium vape boutique POS"

**Positioning**: Bukan POS generik. Aplikasi kasir untuk toko vape premium di Indonesia. Vibe: Rolex × Hublot × streetwear-luxe — onyx pekat, aksen emas, kerja diam-diam mahal.

**Personality**: bold, premium, minimalis, sedikit edgy. Bukan klinik, bukan supermarket, bukan toko serba ada. Kasir merasa sedang menjalankan butik perhiasan.

**Visual signature**: lingkaran onyx dengan ring emas + huruf "K" emas di tengah. Wordmark "KASIR" hitam tebal, "V A P E S T O R E" emas dengan letterspacing lebar.

## Color Palette

### Light Mode (default — cashier daytime)

```
Primary           #1C1917  onyx-900     app bar, headings, body text
PrimaryContainer  #F5F5F4  onyx-100     subtle container
OnPrimary         #FAFAF9  cream-50

Secondary         #EAB308  gold-500     accent (chips, highlights)
SecondaryContainer #FEF3C7 gold-200
OnSecondary       #0C0A09  onyx-950

Tertiary (CTA)    #CA8A04  gold-600     ★ PRIMARY CTA (Bayar, Selesaikan, Simpan)
OnTertiary        #0C0A09  onyx-950     ★ BLACK on GOLD — premium retail contrast
TertiaryContainer #FDE68A  gold-300

Background        #FAFAF9  cream-50     warm off-white main bg
Surface           #FFFFFF              card surface
SurfaceVariant    #F5F5F4  cream-100   subtle card variant
OnSurfaceVariant  #57534E  onyx-600    secondary text

Outline           #A8A29E  onyx-400    visible borders
OutlineVariant    #E7E5E4  onyx-200    subtle dividers

Error             #DC2626  red-600
Success           #16A34A  green-600   (custom — not Material default)
```

### Dark Mode (boutique evening use)

```
Primary           #FAFAF9  cream-50     inverted — headings on dark
PrimaryContainer  #292524  onyx-800

Secondary         #FACC15  gold-400     brighter for dark surfaces
Tertiary (CTA)    #EAB308  gold-500     slightly brighter gold for dark
OnTertiary        #0C0A09  onyx-950

Background        #0C0A09  onyx-950     premium pitch black
Surface           #1C1917  onyx-900
SurfaceVariant    #292524  onyx-800
OnSurfaceVariant  #A8A29E  onyx-400

Outline           #57534E  onyx-600
```

### Brand-locked elements (theme-independent)

These elements **always** use onyx + gold regardless of system theme — they are brand statements, not Material 3 slots:

- **MetricCard.Primary** (omzet hero card): onyx-900→onyx-950 vertical gradient, GOLD-500 value, royal-gold accent bar
- **BrandHeader monogram**: onyx-950→onyx-900 gradient circle + royal-gold ring + GOLD-500 "K"

## Brand-only colors (don't map to Material slots)

```
BrandGoldRoyal   #D4AF37  pure royal gold — hero accents only (monogram ring, accent bars)
BrandGold500     #EAB308  hero value display (omzet number)
BrandGold400     #FACC15  highlight, "→" arrows on dark surfaces
```

Use these sparingly — they're statement pieces. The bulk of the UI should use Material slots (`primary`, `tertiary`, etc.) so theme switching works correctly.

## Typography (Google Fonts via downloadable provider)

| Style | Font | Weight | Size | Usage |
|---|---|---|---|---|
| displayLarge | Rubik | 700 (Black via copy) | 36sp | Setup wizard hero |
| displayMedium | Rubik | 700 (Black via copy on hero) | 28sp | Section banners, omzet value |
| headlineLarge | Rubik | 700 | 24sp | Screen titles |
| headlineMedium | Rubik | 600 (Bold via copy) | 22sp | Dashboard greeting |
| titleLarge | Rubik | 600 | 20sp | TopAppBar title |
| titleMedium | Rubik | 600 (Bold via copy on price) | 16sp | List primary, product price |
| titleSmall | Rubik | 500 | 14sp | Product name, subheaders |
| bodyLarge | Inter | 400 | 16sp | Body text default |
| bodyMedium | Inter | 400 | 14sp | Secondary body |
| bodySmall | Inter | 400 | 12sp | Caption, metadata |
| labelLarge | Inter | 600 | 14sp | Button labels (with letter-spacing 0.5sp) |
| labelMedium | Inter | 500 | 12sp | Section labels (with letter-spacing 1.5-2sp UPPERCASE) |
| labelSmall | Inter | 500 | 11sp | Tagline (letter-spacing 2-6sp) |

**Tracking rule**: section labels (UPPERCASE) get +1.5sp letterspacing. Brand wordmark "VAPESTORE" gets +6sp. Never use letterspacing on body copy.

## Shape Scale

```
ExtraSmall  4dp   — chips, badges
Small       8dp   — small buttons, input fields
Medium      12dp  — cards (default)
Large       14dp  — ProductCard
ExtraLarge  16dp  — StandardCard
Hero        20dp  — MetricCard.Primary (omzet hero), bottom sheets, modals
```

## Elevation

```
Level 0: 0dp   — flat surfaces (chips, dividers)
Level 1: 1dp   — subtle cards (StandardCard)
Level 2: 2dp   — ProductCard
Level 4: 4dp   — raised modals, FABs
Level 6: 6dp   — MetricCard.Primary hero — visual weight signature
```

## Component Recipes

### MetricCard.Primary (hero omzet card)
- Container: `Brush.verticalGradient(BrandOnyx900, BrandOnyx950)`
- Shape: `RoundedCornerShape(20.dp)`
- Elevation: `6.dp`
- Top accent: 3dp × 28dp gold-royal rectangle
- Label: `labelMedium` UPPERCASE, letterspacing 2sp, `cream-50 alpha 65%`
- Value: `displayMedium` FontWeight.Black, `BrandGold500`
- Subtitle: `bodyMedium`, `cream-50 alpha 75%`
- Embedded CTA: TextButton with gold-400 contentColor + arrow icon

### PrimaryButton (Bayar / Selesaikan / Simpan)
- Container: `colorScheme.tertiary` = `gold-600`
- Content: `colorScheme.onTertiary` = `onyx-950` (BLACK on gold)
- Height: `56.dp`
- Label: `titleMedium` UPPERCASE

### BrandHeader monogram
- Circle: 80dp, `Brush.linearGradient(BrandOnyx950, BrandOnyx900)`
- Ring: 1.5dp solid `BrandGoldRoyal`
- "K": Rubik displayLarge 42sp Black, `BrandGold500`

### ProductCard
- Shape: `RoundedCornerShape(14.dp)`
- Elevation: `2.dp`
- Image placeholder gradient: `surfaceVariant → tertiaryContainer` (subtle cream → light gold)
- Price: `titleMedium` Bold, `colorScheme.tertiary` = `gold-600` (retail psychology — price POPS)
- Badges (low stock / out of stock): `errorContainer` / `error` with letterspacing UPPERCASE label

## Do's and Don'ts

### DO
- Use Material slots for 90% of color decisions (`primary`, `tertiary`, etc.)
- Use direct Brand* tokens only for brand-locked elements (hero card, monogram, accent bars)
- Capitalize section labels with +1.5sp letterspacing
- Use `tertiary` (gold) for **price** display in product cards
- Use `tertiary` (gold) for **all primary CTAs** — never `primary` (onyx)

### DON'T
- Don't sprinkle `BrandGold*` colors all over the UI — gold is for CTAs and hero accents
- Don't use bright `BrandGoldRoyal #D4AF37` for body text — it's a hero-only accent
- Don't re-enable Material You dynamic color — `KasirVapestoreTheme` keeps it off intentionally
- Don't use gradient on cards other than the hero MetricCard.Primary — keep it special
- Don't use purple/slate anywhere — those tokens were retired in this rebrand

## Verification Checklist (before merging UI changes)

- [ ] CTA buttons render gold container with black text
- [ ] Omzet card on Dashboard shows onyx gradient + gold value + royal-gold accent bar
- [ ] BrandHeader "K" monogram has visible gold ring on onyx background
- [ ] Product card price renders in gold (`tertiary`), not onyx (`primary`)
- [ ] Dark mode: background is deep onyx-950, surfaces onyx-900
- [ ] No purple/slate tokens left anywhere in `theme/` or component code
- [ ] WCAG AA contrast holds: gold-600 (CTA bg) + onyx-950 (CTA text) = ~13:1 ✓
