/*
 * Copyright 2020 The Little Drops of Rain Project
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

package app.web.diegoflassa_site.littledropsofrain.contracts

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import app.web.diegoflassa_site.littledropsofrain.data.dao.FilesDao
import app.web.diegoflassa_site.littledropsofrain.helpers.FIleUtils
import com.yalantis.ucrop.UCrop
import java.io.File

class CropImageResultContract : ActivityResultContract<Pair<Uri, Pair<Float, Float>>, Uri?>() {

    companion object {
        val ASPECT_RATIO_BOX = Pair(1F, 1F)
        val ASPECT_RATIO_RECTANGLE = Pair(2F, 1F)
        val MAX_IMAGE_SIZE = Pair(512, 256)
    }

    override fun createIntent(context: Context, input: Pair<Uri, Pair<Float, Float>>): Intent {
        return createCropIntent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        var resultUri: Uri? = null
        if (resultCode == RESULT_OK) {
            resultUri = UCrop.getOutput(intent!!)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError: Throwable? = UCrop.getError(intent!!)
            cropError?.printStackTrace()
        }
        return resultUri
    }

    private fun createCropIntent(context: Context, data: Pair<Uri, Pair<Float, Float>>): Intent {
        val pathUri = Uri.parse("file:/" + FIleUtils.getPath(context, data.first))
        val lastIndex = pathUri.toString().lastIndexOf(".")
        var fileExtension: String? = ""
        val fileNameLastIndex = pathUri.toString().lastIndexOf("/")
        var fileName = pathUri.toString().substring(fileNameLastIndex + 1)
        if (lastIndex >= pathUri.toString().length - 5) {
            fileExtension = pathUri.toString().substring(lastIndex)
            fileName = fileName.replace(fileExtension, "")
        }
        val filePath = context.cacheDir.toString() + "/" + FilesDao.CACHE_DIR + "/"
        val fFilePath = File(filePath)
        if (!fFilePath.exists()) {
            fFilePath.mkdirs()
        }
        val tempFile = File.createTempFile(fileName + "_cropped", fileExtension, fFilePath)
        val uriDest = Uri.fromFile(tempFile)
        return UCrop.of(data.first, uriDest)
            .withAspectRatio(data.second.first, data.second.second)
            .withMaxResultSize(MAX_IMAGE_SIZE.first, MAX_IMAGE_SIZE.second)
            .getIntent(context)
    }
}
