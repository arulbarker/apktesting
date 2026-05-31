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

private val InterRegular = gfontFamily(inter, FontWeight.Normal)
private val InterMedium = gfontFamily(inter, FontWeight.Medium)
private val InterSemiBold = gfontFamily(inter, FontWeight.SemiBold)

val KasirTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.25).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = RubikBold, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = RubikSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = RubikSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = RubikSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = RubikSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = RubikMedium, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = InterRegular, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterRegular, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = InterRegular, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = InterSemiBold, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = InterMedium, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = InterMedium, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
    ),
)
