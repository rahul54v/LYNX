package com.lynx.fintech.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Emerald,
    onPrimary = Obsidian,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = TextPrimary,
    secondary = PurpleAccent,
    onSecondary = TextPrimary,
    secondaryContainer = ObsidianElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = BlueAccent,
    onTertiary = TextPrimary,
    tertiaryContainer = ObsidianElevated,
    onTertiaryContainer = TextPrimary,
    background = Obsidian,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianLight,
    onSurfaceVariant = TextSecondary,
    error = ExpenseRed,
    onError = TextPrimary,
    errorContainer = ExpenseRed.copy(alpha = 0.1f),
    onErrorContainer = ExpenseRed,
    outline = DividerDark,
    outlineVariant = CardBorder,
    scrim = Obsidian.copy(alpha = 0.8f)
)

@Composable
fun LynxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Obsidian.toArgb()
            window.navigationBarColor = Obsidian.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LynxTypography,
        content = content
    )
}
