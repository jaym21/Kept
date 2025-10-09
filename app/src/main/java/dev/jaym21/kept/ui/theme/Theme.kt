package dev.jaym21.kept.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jaym21.kept.R


val OutfitFontFamily = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold)
)

val OverpassMonoFontFamily = FontFamily(
    Font(R.font.overpass_mono_regular, FontWeight.Normal),
    Font(R.font.overpass_mono_medium, FontWeight.Medium)
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    tertiary = Success,
    surface = Surface,
    onSurface = onSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = Secondary,
    error = Error,
    onError = OnError,
    outline = Outline
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = Primary
    ),
    displayMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        color = Primary
    ),
    headlineSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = Primary
    ),
    titleMedium = TextStyle(
        fontFamily = OverpassMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        color = Primary
    ),
    bodyLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Primary
    ),
    bodyMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = Secondary
    ),
    labelSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Secondary
    )
)

@Composable
fun KeptTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}