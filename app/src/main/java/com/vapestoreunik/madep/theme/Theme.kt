package com.vapestoreunik.madep.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BrandLightColorScheme = lightColorScheme(
    primary = BrandSlate900,
    onPrimary = BrandStone50,
    primaryContainer = BrandSlate200,
    onPrimaryContainer = BrandSlate900,

    secondary = BrandPurple600,
    onSecondary = BrandStone50,
    secondaryContainer = BrandPurple100,
    onSecondaryContainer = BrandPurple700,

    tertiary = BrandAmber500,
    onTertiary = BrandSlate900,
    tertiaryContainer = BrandAmber100,
    onTertiaryContainer = BrandAmber700,

    background = BrandStone50,
    onBackground = BrandSlate900,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = BrandSlate900,
    surfaceVariant = BrandStone100,
    onSurfaceVariant = BrandStone600,

    error = BrandRed600,
    onError = BrandStone50,
    errorContainer = BrandRed100,
    onErrorContainer = BrandRed600,

    outline = BrandSlate400,
    outlineVariant = BrandSlate200,
)

private val BrandDarkColorScheme = darkColorScheme(
    primary = BrandSlate100,
    onPrimary = BrandSlate900,
    primaryContainer = BrandSlate800,
    onPrimaryContainer = BrandSlate100,

    secondary = BrandPurple400,
    onSecondary = BrandSlate900,
    secondaryContainer = BrandPurple700,
    onSecondaryContainer = BrandPurple100,

    tertiary = BrandAmber400,
    onTertiary = BrandSlate900,
    tertiaryContainer = BrandAmber700,
    onTertiaryContainer = BrandAmber100,

    background = BrandStone950,
    onBackground = BrandStone50,
    surface = BrandStone900,
    onSurface = BrandStone50,
    surfaceVariant = BrandStone800,
    onSurfaceVariant = BrandStone400,

    error = BrandRed400,
    onError = BrandSlate900,
    errorContainer = BrandRed600,
    onErrorContainer = BrandRed100,

    outline = BrandSlate600,
    outlineVariant = BrandSlate700,
)

@Composable
fun KasirVapestoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Brand-locked colors — dynamic color (Material You) disabled
    // supaya brand identity konsisten di semua HP
    val colorScheme = if (darkTheme) BrandDarkColorScheme else BrandLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KasirTypography,
        shapes = KasirShapes,
        content = content,
    )
}
