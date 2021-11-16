/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.presentation.old.ui.font_families

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import app.web.diegoflassa_site.littledropsofrain.R

class FontFamilies {
    companion object {
        val bpScriptFamily = FontFamily(
            Font(R.font.bpscript, FontWeight.Normal),
        )
        val montSerratFamily = FontFamily(
            Font(R.font.montserrat_light, FontWeight.Light),
            Font(R.font.montserrat_regular, FontWeight.Normal),
            Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
            Font(R.font.montserrat_medium, FontWeight.Medium),
            Font(R.font.montserrat_bold, FontWeight.Bold),
            Font(R.font.montserrat_semibold, FontWeight.SemiBold),
            Font(R.font.montserrat_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
        )
    }
}
