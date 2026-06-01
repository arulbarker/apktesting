package com.vapestoreunik.madep.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// ─────────────────────────────────────────────────────────────────────────────
// BLACKOUT YELLOW — single brand-locked scheme.
//
// System dark/light setting is IGNORED. The app always renders in this
// blackout look because the brand IS black + electric yellow. Light mode
// would dilute the identity.
//
// Material 3 slot mapping:
//   background = BrandJet (#0A0A0A)         — the canvas
//   surface    = BrandCarbon (#141414)      — cards
//   primary    = BrandYellow (#FACC15)      — ★ THE brand. Headings, key accents.
//   tertiary   = BrandYellow                — ★ ALL primary CTAs (same yellow)
//   onPrimary / onTertiary = BrandJet       — BLACK text on YELLOW (max punch)
// ─────────────────────────────────────────────────────────────────────────────

private val BlackoutYellowScheme = darkColorScheme(
    primary = BrandYellow,
    onPrimary = BrandJet,
    primaryContainer = BrandYellowDim,
    onPrimaryContainer = BrandYellowHi,

    secondary = BrandYellowHi,
    onSecondary = BrandJet,
    secondaryContainer = BrandSlate,
    onSecondaryContainer = BrandYellow,

    tertiary = BrandYellow,
    onTertiary = BrandJet,
    tertiaryContainer = BrandYellowDeep,
    onTertiaryContainer = BrandJet,

    background = BrandJet,
    onBackground = BrandSnow,

    surface = BrandCarbon,
    onSurface = BrandSnow,

    surfaceVariant = BrandSlate,
    onSurfaceVariant = BrandAsh,

    surfaceContainerLowest = BrandBlack,
    surfaceContainerLow = BrandJet,
    surfaceContainer = BrandCarbon,
    surfaceContainerHigh = BrandSlate,
    surfaceContainerHighest = BrandIron,

    outline = BrandSteel,
    outlineVariant = BrandIron,

    error = BrandRed,
    onError = BrandSnow,
    errorContainer = BrandRedDim,
    onErrorContainer = BrandRed,

    inverseSurface = BrandSnow,
    inverseOnSurface = BrandJet,
    inversePrimary = BrandYellowDeep,
)

@Composable
fun KasirVapestoreTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    // Brand-locked: ignore system theme. Always blackout.
    MaterialTheme(
        colorScheme = BlackoutYellowScheme,
        typography = KasirTypography,
        shapes = KasirShapes,
        content = content,
    )
}
