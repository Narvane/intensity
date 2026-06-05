package com.intensity.mobile.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

private val WarmSurface = Color(0xFFFCFAF7)
private val WarmSurfaceContainer = Color(0xFFF6EFE6)
private val WarmSurfaceContainerHigh = Color(0xFFEFE7DB)
private val OnSurface = Color(0xFF1D1B20)
private val OnSurfaceVariant = Color(0xFF49454F)
private val Outline = Color(0x38291B20)
private val BrownPrimary = Color(0xFFB0946F)
private val OnBrownPrimary = Color(0xFF2A2016)
private val Secondary = Color(0xFF625B71)

private val DarkBackground = Color(0xFF15110E)
private val DarkSurface = Color(0xFF1C1713)
val IntensityCardContainerDark = Color(0xFF231C17)

val IntensityCardContainerLight = Color(0xFFFFFFFF)

val IntensityBackgroundBrushLight = Brush.verticalGradient(
    listOf(Color(0xFFFCFAF7), Color.White)
)

val IntensityBackgroundBrushDark = Brush.verticalGradient(
    listOf(Color(0xFF1F1813), DarkBackground)
)

private val LightColors = lightColorScheme(
    primary = BrownPrimary,
    onPrimary = Color.White,
    primaryContainer = WarmSurfaceContainer,
    onPrimaryContainer = OnBrownPrimary,

    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),

    tertiary = IntensityBrand.ParamEffort,
    onTertiary = Color.White,
    tertiaryContainer = IntensityBrand.ParamEffortSurface,
    onTertiaryContainer = Color(0xFF004D56),

    background = WarmSurface,
    onBackground = OnSurface,
    surface = WarmSurface,
    onSurface = OnSurface,
    surfaceVariant = WarmSurfaceContainer,
    onSurfaceVariant = OnSurfaceVariant,

    outline = Outline,
    outlineVariant = Color(0xFFCAC4D0),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD4BC9A),
    onPrimary = Color(0xFF2A2016),
    primaryContainer = Color(0xFF3D2F22),
    onPrimaryContainer = Color(0xFFF6EFE6),

    secondary = Color(0xFFCAC4D0),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),

    tertiary = Color(0xFF4DD0E1),
    onTertiary = Color(0xFF00363E),
    tertiaryContainer = Color(0xFF004E59),
    onTertiaryContainer = Color(0xFF97F0FF),

    background = DarkBackground,
    onBackground = Color(0xFFECE3DC),
    surface = DarkSurface,
    onSurface = Color(0xFFECE3DC),
    surfaceVariant = Color(0xFF2F2621),
    onSurfaceVariant = Color(0xFFD1C4BB),

    outline = Color(0xFF9B8F87),
    outlineVariant = Color(0xFF4B403A),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)

private val CartoonShapes = Shapes(
    extraSmall = RoundedCornerShape(9.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

private val CozyTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)

@Composable
fun IntensityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = CozyTypography,
        shapes = CartoonShapes,
        content = content
    )
}
