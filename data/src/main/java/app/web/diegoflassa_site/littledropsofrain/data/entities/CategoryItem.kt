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

package app.web.diegoflassa_site.littledropsofrain.data.entities

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.HashMap

@Keep
@Parcelize
@Suppress("Unchecked_Cast")
data class CategoryItem(
    var uid: String? = null,
    var imageUrl: String? = null,
    var category: String = "",
    var products: List<String> = listOf()
) : Parcelable {
    companion object {
        const val UID = "uid"
        const val IMAGE = "imageUrl"
        const val CATEGORY = "category"
        const val PRODUCTS = "products"
    }

    constructor(map: Map<String, Any>) : this() {
        fromMap(map)
    }

    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result[UID] = uid
        result[IMAGE] = imageUrl
        result[CATEGORY] = category
        result[PRODUCTS] = products
        return result
    }

    private fun fromMap(map: Map<String, Any>) {
        uid = map[UID] as String?
        imageUrl = map[IMAGE] as String?
        category = map[CATEGORY] as String
        products = map[PRODUCTS] as List<String>
    }

    fun getImageUrlAsUri(): Uri? {
        var ret: Uri? = null
        if (imageUrl != null) {
            ret = Uri.parse(imageUrl)
        }
        return ret
    }
}
