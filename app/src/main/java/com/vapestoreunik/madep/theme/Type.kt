package com.vapestoreunik.madep.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.vapestoreunik.madep.R

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val rubik = GoogleFont("Rubik")
private val inter = GoogleFont("Inter")

private fun gfontFamily(font: GoogleFont, weight: FontWeight) = FontFamily(
    Font(googleFont = font, fontProvider = googleFontProvider, weight = weight, style = FontStyle.Normal),
)

private val RubikMedium = gfontFamily(rubik, FontWeight.Medium)
private val RubikSemiBold = gfontFamily(rubik, FontWeight.SemiBold)
private val RubikBold = gfontFamily(rubik, FontWeight.Bold)
private val RubikBlack = gfontFamily(rubik, FontWeight.Black)

private val InterRegular = gfontFamily(inter, FontWeight.Normal)
private val InterMedium = gfontFamily(inter, FontWeight.Medium)
private val InterSemiBold = gfontFamily(inter, FontWeight.SemiBold)
private val InterBold = gfontFamily(inter, FontWeight.Bold)

// ─────────────────────────────────────────────────────────────────────────────
// BLACKOUT YELLOW typography — bigger, bolder, more aggressive letterspacing.
// Displays bumped 33% over Material 3 defaults. Labels track wider (UPPERCASE
// section headers feel like nightclub signage).
// ─────────────────────────────────────────────────────────────────────────────

val KasirTypography = Typography(
    // Display — for HERO numbers (omzet, prices), splash text
    displayLarge = TextStyle(
        fontFamily = RubikBlack, fontWeight = FontWeight.Black,
        fontSize = 56.sp, lineHeight = 60.sp, letterSpacing = (-1.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = RubikBlack, fontWeight = FontWeight.Black,
        fontSize = 44.sp, lineHeight = 48.sp, letterSpacing = (-1).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp,
    ),

    // Headline — for screen titles, big greetings
    headlineLarge = TextStyle(
        fontFamily = RubikBlack, fontWeight = FontWeight.Black,
        fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 30.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp,
    ),

    // Title — for app bars, list-item primary
    titleLarge = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = RubikSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = RubikMedium, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),

    // Body — Inter for legibility
    bodyLarge = TextStyle(
        fontFamily = InterRegular, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.3.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterRegular, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = InterRegular, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
    ),

    // Label — aggressive tracking on UPPERCASE forms ("L I K E  T H I S")
    labelLarge = TextStyle(
        fontFamily = InterBold, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 1.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = InterSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 2.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = InterSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 2.sp,
    ),
)
