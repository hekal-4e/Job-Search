import androidx.compose.ui.graphics.Color

// Purple theme colors
object PurpleTheme {
    val Primary = Color(0xFF6200EE)
    val PrimaryContainer = Color(0xFF3700B3)
    val Secondary = Color(0xFF03DAC6)
    val SecondaryContainer = Color(0xFF018786)
    val Background = Color(0xFF121212)
    val Surface = Color(0xFF121212)
    val SurfaceVariant = Color(0xFF1F1F1F)
    val Error = Color(0xFFCF6679)

    val OnPrimary = Color.White
    val OnPrimaryContainer = Color.White
    val OnSecondary = Color.Black
    val OnSecondaryContainer = Color.White
    val OnBackground = Color.White
    val OnSurface = Color.White
    val OnSurfaceVariant = Color(0x9AA27878)  // 87% white
    val OnError = Color.Black
}

// Orange theme colors
object OrangeTheme {
    val Primary = Color(0xFFFF5722)
    val PrimaryContainer = Color(0xFFE64A19)
    val Secondary = Color(0xFFFF9800)
    val SecondaryContainer = Color(0xFFF57C00)
    val Background = Color.White
    val Surface = Color.White
    val SurfaceVariant = Color(0x36F5F5F5)
    val Error = Color(0xFFCF6679)
    val Orange = Color(0xFFFF9800)          // Accent color for icons and buttons
    val Purple80 = Color(0xFFD0BCFF)
    val Purple40 = Color(0xFF6650a4)

    val OnPrimary = Color.White
    val OnPrimaryContainer = Color.White
    val OnSecondary = Color.Black
    val OnSecondaryContainer = Color.White
    val OnBackground = Color.White
    val OnSurface = Color.White
    val OnSurfaceVariant = Color(0xDE9B7171)  // 87% white
    val OnError = Color.Black
}

// Semantic colors
object AppColors {
    val black = Color(0xFF121212)

    // Text colors with different emphasis levels
    val TextPrimary = Color(0xFF121212)
    val TextSecondary = Color(0xB3D5A5A5)  // 70% white
    val TextDisabled = Color(0x61FFFFFF)  // 38% white

    // Surface colors for different elevations
    val SurfaceLevel0 = Color(0xFF121212)  // Same as theme background
    val SurfaceLevel1 = Color(0xFF1F1F1F)
    val SurfaceLevel2 = Color(0xFF2C2C2C)
    val SurfaceLevel3 = Color(0xFF393939)

    // Overlay colors
    val Scrim = Color(0x52000000)  // 32% black
    val Overlay = Color(0x0DFFFFFF)  // 5% white
}
