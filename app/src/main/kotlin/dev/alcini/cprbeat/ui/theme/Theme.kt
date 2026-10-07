package dev.alcini.cprbeat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Design tokens from design/tokens.md. Always dark, high contrast, no dynamic color. */
object CprColor {
    val Background = Color(0xFF0A0B0D)
    val Surface = Color(0xFF16181C)
    val SurfaceHigh = Color(0xFF1B1D21)
    val Outline = Color(0xFF5A606B)
    val OutlineSubtle = Color(0xFF3A3E46)
    val OnBackground = Color(0xFFF5F6F7)
    val OnMuted = Color(0xFFA9AEB7)
    val Selected = Color(0xFFF5F6F7)
    val OnSelected = Color(0xFF0A0B0D)
    val Beat = Color(0xFFFFC233)
    val OnBeat = Color(0xFF1A1300)
    val TickIdle = Color(0xFF2E3137)
    val TickDone = Color(0xFFF5F6F7)
    val Warning = Color(0xFFFF3B30)
    val Stop = Color(0xFFD32F2F)
    val Breathe = Color(0xFF5CC8FF)
}

object CprSize {
    val MinTouch = 64.dp
    val Gear = 64.dp
    val TimerBlock = 128.dp
    val Segmented = 64.dp
    val SwitchBanner = 88.dp
    val StartCircle = 300.dp
    val BeatArea = 272.dp
    val BeatDisk = 216.dp
    val StopHeight = 64.dp
    val StopWidth = 220.dp
    val RateButton = 80.dp
    val SettingsOption = 64.dp
    val Edge = 24.dp
}

private val DarkColors = darkColorScheme(
    primary = CprColor.Beat,
    onPrimary = CprColor.OnBeat,
    secondary = CprColor.Breathe,
    onSecondary = Color.Black,
    error = CprColor.Stop,
    background = CprColor.Background,
    onBackground = CprColor.OnBackground,
    surface = CprColor.Surface,
    onSurface = CprColor.OnBackground,
    surfaceVariant = CprColor.SurfaceHigh,
    onSurfaceVariant = CprColor.OnMuted,
    outline = CprColor.Outline,
)

private val CprTypography = Typography(
    displayLarge = TextStyle(fontSize = 120.sp, fontWeight = FontWeight.Bold, lineHeight = 120.sp),
    displayMedium = TextStyle(fontSize = 88.sp, fontWeight = FontWeight.SemiBold, lineHeight = 88.sp),
    displaySmall = TextStyle(fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 56.sp),
    headlineMedium = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.ExtraBold),
    headlineSmall = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontSize = 17.sp),
    bodyMedium = TextStyle(fontSize = 15.sp),
    labelLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
    labelMedium = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
)

@Composable
fun CprBeatTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = CprTypography, content = content)
}
