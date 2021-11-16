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

package app.web.diegoflassa_site.littledropsofrain.presentation.old.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.web.diegoflassa_site.littledropsofrain.R
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@ExperimentalCoilApi
class OrEnterWith {

    @Composable
    @Preview
    fun Show() {
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {

            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Divider(color = Color.Black, thickness = 1.dp)
                Text(text = stringResource(R.string.or_enter_with))
                Divider(color = Color.Black, thickness = 1.dp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = rememberImagePainter(R.drawable.image_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(width = 90.dp, height = 60.dp)
                )
                Image(
                    painter = rememberImagePainter(R.drawable.image_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(width = 90.dp, height = 60.dp)
                )
                Image(
                    painter = rememberImagePainter(R.drawable.image_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(width = 90.dp, height = 60.dp)
                )
            }
        }
    }
}
