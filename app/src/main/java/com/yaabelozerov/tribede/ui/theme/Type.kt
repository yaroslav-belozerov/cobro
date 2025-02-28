package com.yaabelozerov.tribede.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yaabelozerov.tribede.R

private val baseline = Typography()

private val bodyFontFamily =
    FontFamily(
        Font(R.font.commissioner_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(R.font.commissioner_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
        Font(R.font.commissioner_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
        Font(R.font.commissioner_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal))

private val displayFontFamily =
    FontFamily(
        Font(R.font.geologica_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
        Font(R.font.geologica_extrabold, weight = FontWeight.ExtraBold, style = FontStyle.Normal),
        Font(R.font.geologica_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
        Font(R.font.geologica_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(R.font.commissioner_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal)
    )

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = bodyFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = bodyFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = bodyFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)