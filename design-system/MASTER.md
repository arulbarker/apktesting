# Kasir Vapestore — Brand Master

> **BLACKOUT YELLOW** — source of truth. Page-specific overrides di `pages/<name>.md`.

## Brand Identity

**Tagline (internal)**: "Pure black + electric yellow. Vape culture, not jewelry counter."

**Positioning**: POS untuk toko vape modern Indonesia. Vibe: streetwear-luxe × hypebeast × cyberpunk-premium. Bukan butik perhiasan, bukan klinik, bukan supermarket. Dipakai 8 jam non-stop tanpa kasir kelelahan secara visual karena canvas pekat dan aksen kuning hanya di titik perlu.

**Personality**: bold, electric, punchy, streetwear-luxe. Identity dipisah jelas dari aplikasi cashier generik.

**Visual signature**:
- Monogram lingkaran 100dp HITAM dengan ring KUNING ELECTRIC 3dp + huruf "K" kuning 56sp Black weight
- Wordmark "KASIR" putih Black, accent rule 48×2dp KUNING di antara, "V A P E S T O R E" kuning letterspaced 8sp
- 5dp left-edge YELLOW STRIPE pada hero MetricCard (signature non-negotiable)
- PrimaryButton = YELLOW PILL CAPSULE 60dp tinggi, teks HITAM UPPERCASE BLACK weight

## Color Palette (single brand-locked scheme)

```
─── BLACK SCALE (canvas + surfaces + text) ────────────────────────
BrandBlack       #000000  pure — accent strokes only
BrandJet         #0A0A0A  ★ main background — near-black warm
BrandCarbon      #141414  ★ card surface
BrandSlate       #1F1F1F  raised surface (FAB, dialog)
BrandIron        #2A2A2A  hover/press, subtle dividers
BrandSteel       #404040  visible outlines
BrandSmoke       #71717A  muted/helper text
BrandAsh         #A1A1AA  secondary text
BrandSnow        #FAFAFA  ★ primary text on dark

─── ELECTRIC YELLOW (THE brand color) ─────────────────────────────
BrandYellow      #FACC15  ★ PRIMARY BRAND — buttons, hero numbers, accents
BrandYellowHi    #FDE047  highlight / hover
BrandYellowDeep  #EAB308  pressed / active
BrandYellowGlow  #FFFB47  ultra-bright — thin strokes, edge stripes
BrandYellowDim   #713F12  muted yellow container (rare)

─── STATUS ────────────────────────────────────────────────────────
BrandRed         #EF4444  error
BrandRedDim      #7F1D1D  error container
BrandGreen       #22C55E  success
BrandGreenDim    #14532D  success container
```

## Material 3 Slot Mapping

```
background       = BrandJet (#0A0A0A)        the canvas
surface          = BrandCarbon (#141414)     cards
surfaceVariant   = BrandSlate (#1F1F1F)      raised cards

primary          = BrandYellow (#FACC15)     ★ brand color — headings, key accents
onPrimary        = BrandJet (#0A0A0A)        ★ BLACK on YELLOW (max contrast)
primaryContainer = BrandYellowDim
onPrimaryContainer = BrandYellowHi

tertiary         = BrandYellow               ★ ALL primary CTAs (same yellow)
onTertiary       = BrandJet                  black text on yellow buttons

outline          = BrandSteel
outlineVariant   = BrandIron

error            = BrandRed
```

**Light/dark system setting is IGNORED**. The app always renders blackout. Light mode would dilute the identity — `KasirVapestoreTheme` accepts a `darkTheme` parameter but discards it.

## Typography

Display weights are **+33% bigger** than Material 3 defaults. UPPERCASE labels track wider (2.5–3sp) for "nightclub signage" feel.

| Style | Font | Weight | Size | Letterspace | Usage |
|---|---|---|---|---|---|
| displayLarge | Rubik Black | 900 | 56sp | -1.5sp | Splash hero |
| displayMedium | Rubik Black | 900 | 44sp | -1sp | Omzet hero number |
| displaySmall | Rubik Bold | 700 | 32sp | -0.5sp | Surface card big numbers |
| headlineLarge | Rubik Black | 900 | 28sp | -0.5sp | Dashboard greeting |
| headlineMedium | Rubik Bold | 700 | 24sp | — | Step titles |
| titleLarge | Rubik Bold | 700 | 20sp | — | App bar |
| titleMedium | Rubik SemiBold | 600 | 16sp | 0.15sp | List primary, price |
| titleSmall | Rubik Medium | 500 | 14sp | 0.1sp | Product name |
| bodyLarge | Inter | 400 | 16sp | 0.3sp | Body |
| bodyMedium | Inter | 400 | 14sp | 0.25sp | Secondary body |
| bodySmall | Inter | 400 | 12sp | 0.4sp | Captions |
| labelLarge | Inter Bold | 700 | 14sp | 1.2sp | Button labels (UPPERCASE) |
| labelMedium | Inter SemiBold | 600 | 12sp | 2sp | Section labels (UPPERCASE) |
| labelSmall | Inter SemiBold | 600 | 11sp | 2sp | Step indicator, tagline |

## Shape Scale

```
extraSmall  6dp   — badges, small chips
small       10dp  — input fields
medium      14dp  — ProductCard
large       20dp  — MetricCard.Primary (hero)
extraLarge  28dp  — bottom sheets, dialogs

PillShape   50%   — PrimaryButton, SecondaryButton (capsule)
```

## Elevation Strategy

Most cards use **0dp elevation** + **1dp border** (BrandIron or outlineVariant) instead of Material shadows. Shadows look weird on pure black backgrounds. Borders define separation cleanly.

The exception is interactive press states — Compose handles those via state-layer ripple.

## Component Recipes

### MetricCard.Primary (hero)
- Shape `RoundedCornerShape(20.dp)`, border 1dp `BrandCarbon`
- **5dp left ELECTRIC YELLOW edge stripe** (full card height, `BrandYellowGlow`)
- Label `labelMedium` UPPERCASE, letterspace 3sp, `BrandAsh`
- Value `displayMedium` Black weight, **`BrandYellow`** ★
- Subtitle `bodyMedium`, snow 75% alpha
- Padding 22dp vertical, 24dp horizontal

### PrimaryButton (Bayar / Selesaikan / Simpan)
- Shape `PillShape` (capsule)
- Container `BrandYellow`, content `BrandJet`
- Height **60dp** (was 56dp — bigger for kasir's busy fingers)
- Label `titleMedium` Black weight UPPERCASE, letterspace 1.5sp

### SecondaryButton
- Shape `PillShape`
- 1.5dp border `BrandYellow`, content `BrandYellow`
- Height 52dp

### ProductCard
- Shape `RoundedCornerShape(16.dp)`, border 1dp `BrandIron`, no elevation
- Container `BrandCarbon`
- Thumbnail: `BrandIron` slab, 28×4dp yellow corner accent (top-right) — like a streetwear hangtag
- **Price is a YELLOW CHIP** — `BrandYellow` background, rounded 8dp, `BrandJet` Black weight text inside
- Brand line UPPERCASE letterspace 1.2sp `BrandAsh`
- Out-of-stock: bottom red strip with "H A B I S" letterspaced 4sp

### BrandHeader monogram
- 100dp circle, radial gradient `BrandJet → BrandBlack` fill
- 3dp `BrandYellow` border ring
- "K" inside: Rubik 56sp Black weight, `BrandYellow`
- Wordmark "KASIR" 44sp Black `BrandSnow`
- 48×2dp accent rule `BrandYellowGlow`
- "V A P E S T O R E" Inter Bold 14sp letterspace 8sp, `BrandYellow`

### SectionHeader
- 4dp × 18dp yellow vertical bar (`BrandYellow`) at left
- 10dp gap
- Title UPPERCASE labelMedium letterspace 2.5sp
- Optional trailing yellow TextButton action

### PIN dots (PinLoginScreen)
- 20dp circle, 2dp border
- Empty: bg `BrandJet`, border `BrandIron`
- Filled: bg `BrandYellow`, border `BrandYellow`
- 14dp gap between dots

## Do's and Don'ts

### DO
- Use **YELLOW liberally** for accents (chips, dots, bars, hero values, prices) — it IS the brand
- Use **PrimaryButton (pill yellow)** for every "this is the primary action" CTA
- Use **0dp elevation + border** for cards
- Use UPPERCASE labels with 2–3sp tracking for section headers
- Test that hero numbers (omzet, prices) feel "louder" than surrounding UI

### DON'T
- Don't re-add cream/white as a background — that was the old palette
- Don't re-enable system dark/light switching — brand-locked is intentional
- Don't use Material shadows on cards (use borders instead)
- Don't dilute yellow with low alphas everywhere — keep it crisp at 100% where used
- Don't introduce purple/slate/amber-gold tokens — those palettes are retired

## Verification Checklist (before merging UI changes)

- [ ] App canvas is BLACK (not cream/white)
- [ ] Primary CTA buttons are YELLOW PILL CAPSULES with BLACK text
- [ ] Dashboard omzet card shows 5dp yellow left edge + value in yellow
- [ ] Product price renders as a YELLOW CHIP, not plain text
- [ ] PIN dots: hollow yellow→solid yellow as digits entered
- [ ] BrandHeader: black monogram circle with visible thick yellow ring
- [ ] No purple/slate/amber-gold tokens anywhere in `theme/` or component code
- [ ] WCAG AA holds: yellow (#FACC15) on black (#0A0A0A) = ~14.7:1 ✓
