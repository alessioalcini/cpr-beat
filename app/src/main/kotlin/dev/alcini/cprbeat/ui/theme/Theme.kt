package dev.alcini.cprbeat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Always dark, high contrast: the app is used in emergencies, often outdoors. */
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF5252),
    onPrimary = Color.Black,
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
)

@Composable
fun CprBeatTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}
