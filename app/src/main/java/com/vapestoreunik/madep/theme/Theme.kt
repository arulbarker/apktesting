package com.vapestoreunik.madep.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Material 3 color scheme — Onyx + Gold (Premium Vape Boutique)
//
// Slot mapping rationale:
//   primary   = onyx900 → app bars, headings, body text (the "black")
//   tertiary  = gold600 → ★ PRIMARY CTA (Bayar/Selesaikan) — onTertiary is onyx
//                         so the button reads "BLACK ON GOLD" (premium retail)
//   secondary = gold500 → accent/highlight (chips, secondary actions)
//
// Dynamic color (Material You) is intentionally DISABLED so brand identity
// is locked across all devices.
// ─────────────────────────────────────────────────────────────────────────────

private val BrandLightColorScheme = lightColorScheme(
    // Primary — deep onyx black
    primary = BrandOnyx900,
    onPrimary = BrandCream50,
    primaryContainer = BrandOnyx100,
    onPrimaryContainer = BrandOnyx900,

    // Secondary — gold accent (chips, highlights, secondary actions)
    secondary = BrandGold500,
    onSecondary = BrandOnyx950,
    secondaryContainer = BrandGold200,
    onSecondaryContainer = BrandGold700,

    // Tertiary — PRIMARY CTA gold (button container)
    tertiary = BrandGold600,
    onTertiary = BrandOnyx950, // BLACK on gold — premium retail contrast
    tertiaryContainer = BrandGold300,
    onTertiaryContainer = BrandGold700,

    // Surfaces — warm cream
    background = BrandCream50,
    onBackground = BrandOnyx900,
    surface = Color.White,
    onSurface = BrandOnyx900,
    surfaceVariant = BrandOnyx100,
    onSurfaceVariant = BrandOnyx600,

    // Status
    error = BrandRed600,
    onError = BrandCream50,
    errorContainer = BrandRed100,
    onErrorContainer = BrandRed600,

    // Outlines
    outline = BrandOnyx400,
    outlineVariant = BrandOnyx200,
)

private val BrandDarkColorScheme = darkColorScheme(
    // Primary — cream (inverted, used for headings on dark bg)
    primary = BrandCream50,
    onPrimary = BrandOnyx900,
    primaryContainer = BrandOnyx800,
    onPrimaryContainer = BrandCream50,

    // Secondary — brighter gold for dark surfaces
    secondary = BrandGold400,
    onSecondary = BrandOnyx950,
    secondaryContainer = BrandOnyx800,
    onSecondaryContainer = BrandGold200,

    // Tertiary — gold CTA, slightly brighter for dark contrast
    tertiary = BrandGold500,
    onTertiary = BrandOnyx950,
    tertiaryContainer = BrandGold700,
    onTertiaryContainer = BrandGold200,

    // Surfaces — premium onyx
    background = BrandOnyx950,
    onBackground = BrandCream50,
    surface = BrandOnyx900,
    onSurface = BrandCream50,
    surfaceVariant = BrandOnyx800,
    onSurfaceVariant = BrandOnyx400,

    // Status
    error = BrandRed400,
    onError = BrandOnyx950,
    errorContainer = BrandRed600,
    onErrorContainer = BrandRed100,

    // Outlines
    outline = BrandOnyx600,
    outlineVariant = BrandOnyx700,
)

@Composable
fun KasirVapestoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) BrandDarkColorScheme else BrandLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KasirTypography,
        shapes = KasirShapes,
        content = content,
    )
}
