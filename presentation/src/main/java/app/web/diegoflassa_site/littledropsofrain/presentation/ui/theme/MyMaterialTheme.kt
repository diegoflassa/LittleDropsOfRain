package app.web.diegoflassa_site.littledropsofrain.presentation.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import app.web.diegoflassa_site.littledropsofrain.presentation.fonts.FontFamilies

class MyMaterialTheme {
    companion object {
        @Composable
        fun ApplyTheme(content: @Composable () -> Unit) {
            return MaterialTheme(typography = FontFamilies.typography, content = content)
        }
    }
}