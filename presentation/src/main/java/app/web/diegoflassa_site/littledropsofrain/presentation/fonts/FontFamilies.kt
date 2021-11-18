package app.web.diegoflassa_site.littledropsofrain.presentation.fonts

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import app.web.diegoflassa_site.littledropsofrain.R

class FontFamilies {
    companion object {
        private val fonts = FontFamily(
            Font(R.font.montserrat_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
            Font(R.font.montserrat_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
            Font(R.font.montserrat_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
            Font(R.font.montserrat_light, weight = FontWeight.Light, style = FontStyle.Normal),
            Font(R.font.montserrat_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
            Font(R.font.montserrat_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
            Font(R.font.montserrat_semibold_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
        )
        private val defaultTypography = Typography()
        val typography = Typography(
            h1 = defaultTypography.h1.copy(fontFamily = fonts),
            h2 = defaultTypography.h2.copy(fontFamily = fonts),
            h3 = defaultTypography.h3.copy(fontFamily = fonts),
            h4 = defaultTypography.h4.copy(fontFamily = fonts),
            h5 = defaultTypography.h5.copy(fontFamily = fonts),
            h6 = defaultTypography.h6.copy(fontFamily = fonts),
            subtitle1 = defaultTypography.subtitle1.copy(fontFamily = fonts),
            subtitle2 = defaultTypography.subtitle2.copy(fontFamily = fonts),
            body1 = defaultTypography.body1.copy(fontFamily = fonts),
            body2 = defaultTypography.body2.copy(fontFamily = fonts),
            button = defaultTypography.button.copy(fontFamily = fonts),
            caption = defaultTypography.caption.copy(fontFamily = fonts),
            overline = defaultTypography.overline.copy(fontFamily = fonts)
        )
    }
}