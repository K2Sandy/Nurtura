package com.tumbuhsehat.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------
// Palette
// Brand: light pink accent, warm off-white background (never a flat gray
// app). Cards are pure white so they lift off the tinted background, or a
// soft pink wash for "hero" moments - the same trick used in the reference
// app (pale tinted card on white bg, here inverted: white card on pale bg).
// ---------------------------------------------------------------------

val PinkPrimary = Color(0xFFD4577E)
val PinkPrimaryDark = Color(0xFFBD4470)
val PinkSoft = Color(0xFFFDF1F1)      // hero-card wash
val PinkSoftStrong = Color(0xFFF7D2DF) // icon-badge fill, slightly stronger
val PinkBorder = Color(0xFFF0BFD0)

// Kept as aliases so existing call sites (PinkContainer, PinkContainerBorder)
// keep working without a big rename pass.
val PinkContainer = PinkSoft
val PinkContainerBorder = PinkBorder

val StatusNormalBg = Color(0xFFE7F4DE)
val StatusNormalText = Color(0xFF3C6B1C)
val StatusWarnBg = Color(0xFFFCEFD6)
val StatusWarnText = Color(0xFF8A5A00)
val StatusAlertBg = Color(0xFFFBE1E1)
val StatusAlertText = Color(0xFF9A2B2B)

val ScreenBackground = Color(0xFFFFFFFF) // warm off-white, not stark white
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceNeutral = Color(0xFFFFFFFF)    // content cards: pure white on the tinted bg
val BorderNeutral = Color(0xFFF0E7E9)
val TextPrimary = Color(0xFF262022)
val TextSecondary = Color(0xFF6E6469)
val TextMuted = Color(0xFF9C9296)

// ---------------------------------------------------------------------
// Shape language - noticeably rounder than default Material, matching the
// friendlier feel of the reference app.
// ---------------------------------------------------------------------

val ShapeHero = RoundedCornerShape(28.dp)
val ShapeCard = RoundedCornerShape(20.dp)
val ShapeChip = RoundedCornerShape(14.dp)
val ShapeSmall = RoundedCornerShape(10.dp)
val ShapePill = RoundedCornerShape(50)

private val LightColors = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkSoft,
    onPrimaryContainer = PinkPrimaryDark,
    secondary = PinkPrimaryDark,
    background = ScreenBackground,
    onBackground = TextPrimary,
    surface = ScreenBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceNeutral,
    onSurfaceVariant = TextSecondary,
    outline = BorderNeutral,
    error = StatusAlertText,
)

private val AppTypography = Typography(
    displaySmall = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 30.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 27.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

@Composable
fun TumbuhSehatTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content,
    )
}
