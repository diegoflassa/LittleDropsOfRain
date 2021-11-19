package app.web.diegoflassa_site.littledropsofrain.presentation.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.web.diegoflassa_site.littledropsofrain.presentation.fonts.FontFamilies


private val primaryColor = Color(0xFFD0DBDA)
private val secondaryColor = Color(0xFFACCDC9)
private val surfaceColor = Color(0xFFF4F4F4)
private val onSurfaceColor = Color(0xFF3E4958)
private val primaryVariantColor = Color(0xFFEE7ABE)

val craneColors = lightColors(
    primary = primaryColor,
    secondary = secondaryColor,
    surface = surfaceColor,
    onSurface = onSurfaceColor,
    primaryVariant = primaryVariantColor,
)

@Composable
fun LittleDropsOfRainTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = craneColors, typography = FontFamilies.typography) {
        content()
    }
}