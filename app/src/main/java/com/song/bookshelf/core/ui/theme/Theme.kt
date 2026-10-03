package com.song.bookshelf.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 따뜻한 서재 느낌: 원목 브라운 + 크림. 다이내믹 컬러는 쓰지 않는다(책장 색이 기기 배경화면에 따라 바뀌지 않도록).

private val LightColors = lightColorScheme(
    primary = Color(0xFF7A4B26),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCC2),
    onPrimaryContainer = Color(0xFF2E1500),
    secondary = Color(0xFF745944),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDCC2),
    onSecondaryContainer = Color(0xFF2A1707),
    tertiary = Color(0xFF5E6135),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFBF5EC),
    onBackground = Color(0xFF211A14),
    surface = Color(0xFFFBF5EC),
    onSurface = Color(0xFF211A14),
    surfaceVariant = Color(0xFFF2DFD1),
    onSurfaceVariant = Color(0xFF51443A),
    surfaceContainer = Color(0xFFF4EBDF),
    outline = Color(0xFF847469),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFF2B98A),
    onPrimary = Color(0xFF4A2709),
    primaryContainer = Color(0xFF63391A),
    onPrimaryContainer = Color(0xFFFFDCC2),
    secondary = Color(0xFFE3C0A5),
    onSecondary = Color(0xFF422C1A),
    secondaryContainer = Color(0xFF5B422E),
    onSecondaryContainer = Color(0xFFFFDCC2),
    tertiary = Color(0xFFC7C994),
    onTertiary = Color(0xFF30330B),
    background = Color(0xFF1E1813),
    onBackground = Color(0xFFEDE0D6),
    surface = Color(0xFF1E1813),
    onSurface = Color(0xFFEDE0D6),
    surfaceVariant = Color(0xFF51443A),
    onSurfaceVariant = Color(0xFFD6C3B5),
    surfaceContainer = Color(0xFF2A221B),
    outline = Color(0xFF9F8D81),
)

@Composable
fun BookshelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
