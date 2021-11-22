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
package app.web.diegoflassa_site.littledropsofrain.data.repository

import android.net.Uri
import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.dao.FilesDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.MessagesDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.ProductsDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.*
import com.google.firebase.firestore.DocumentReference
import java.util.*

class FilesRepository(private val filesDao: FilesDao) {

    private val tag: String? = FilesRepository::class.simpleName

    fun remove(image: Uri,
               listener: OnFileUploadedListener? = null,
               failureListener: OnFileUploadedFailureListener? = null,
               isUserAvatar: Boolean = false) {
        Log.i(tag, "remove")
        filesDao.insert(image, listener, failureListener, isUserAvatar)
    }

    fun insert(image: Uri?) {
        Log.i(tag, "insert")
        filesDao.remove(image)
    }
}
