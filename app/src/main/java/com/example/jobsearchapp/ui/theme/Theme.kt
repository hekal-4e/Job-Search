package com.example.jobsearchapp.ui.theme

import OrangeTheme.Orange
import OrangeTheme.Purple40
import OrangeTheme.Purple80
import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PurpleColorScheme = darkColorScheme(
    primary = Purple40,
    onPrimary = PurpleTheme.OnPrimary,
    primaryContainer = PurpleTheme.PrimaryContainer,
    onPrimaryContainer = PurpleTheme.OnPrimaryContainer,
    secondary = Purple80,
    onSecondary = PurpleTheme.OnSecondary,
    secondaryContainer = PurpleTheme.SecondaryContainer,
    onSecondaryContainer = PurpleTheme.OnSecondaryContainer,
    background = PurpleTheme.Background,
    onBackground = PurpleTheme.OnBackground,
    surface = PurpleTheme.Surface,
    onSurface = PurpleTheme.OnSurface,
    surfaceVariant = PurpleTheme.SurfaceVariant,
    onSurfaceVariant = PurpleTheme.OnSurfaceVariant,
    error = PurpleTheme.Error,
    onError = PurpleTheme.OnError
)

private val OrangeColorScheme = darkColorScheme(
    primary = OrangeTheme.Primary,
    onPrimary = OrangeTheme.OnPrimary,
    primaryContainer = OrangeTheme.PrimaryContainer,
    onPrimaryContainer = OrangeTheme.OnPrimaryContainer,
    onSecondary = OrangeTheme.OnSecondary,
    secondaryContainer = OrangeTheme.SecondaryContainer,
    onSecondaryContainer = OrangeTheme.OnSecondaryContainer,
    background = OrangeTheme.Background,
    secondary = Orange,
    onBackground = OrangeTheme.OnBackground,
    surface = OrangeTheme.Surface,
    onSurface = OrangeTheme.OnSurface,
    surfaceVariant = OrangeTheme.SurfaceVariant,
    onSurfaceVariant = OrangeTheme.OnSurfaceVariant,
    error = OrangeTheme.Error,
    onError = OrangeTheme.OnError
)

@Composable
fun JobSearchAppTheme(
    isPurpleTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isPurpleTheme) PurpleColorScheme else OrangeColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
