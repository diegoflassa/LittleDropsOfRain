package app.web.diegoflassa_site.littledropsofrain.presentation.ui.composables

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