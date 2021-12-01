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
import java.util.*

@Keep
@Suppress("UNUSED")
enum class Source(private val source: String) {
    ILURIA("iluria"),
    ETSY("etsy"),
    UNKNOWN("Unknown");

    override fun toString(): String {
        return source
    }
}

@Keep
@Parcelize
@Suppress("Unchecked_Cast")
data class Product(
    var uid: String? = null,
    var idIluria: String? = null,
    var idSource: String? = null,
    var linkProduct: String? = null,
    var title: String? = null,
    var price: Int? = null,
    @field:JvmField var isPublished: Boolean = false,
    @field:JvmField var isPublishedSource: String? = null,
    var installment: String? = null,
    var disponibility: String? = null,
    var imageUrl: String? = null,
    var likes: MutableList<String> = ArrayList(),
    var source: String? = Source.UNKNOWN.toString(),
    var categories: MutableList<String> = ArrayList()
) : Parcelable {

    companion object {
        const val UID = "uid"
        const val ID_ILURIA = "idIluria"
        const val ID_SOURCE = "idSource"
        private const val LINK_PRODUCT = "linkProduct"
        private const val TITLE = "title"
        const val PRICE = "price"
        const val IS_PUBLISHED = "isPublished"
        // Compound key, used in the queries
        const val IS_PUBLISHED_SOURCE = "isPublishedSource"
        private const val INSTALLMENT = "installment"
        private const val DISPONIBILITY = "disponibility"
        private const val IMAGE_URL = "imageUrl"
        const val LIKES = "likes"
        const val SOURCE = "source"
        const val CATEGORIES: String = "categories"
    }

    constructor(map: Map<String, Any>) : this() {
        fromMap(map)
    }

    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result[UID] = uid
        result[ID_ILURIA] = idIluria
        result[ID_SOURCE] = idSource
        result[LINK_PRODUCT] = linkProduct
        result[TITLE] = title
        result[PRICE] = price
        result[IS_PUBLISHED] = isPublished
        result[IS_PUBLISHED_SOURCE] = (isPublished.toString() + "_" + source)
        result[INSTALLMENT] = installment
        result[DISPONIBILITY] = disponibility
        result[IMAGE_URL] = imageUrl
        result[LIKES] = likes
        result[SOURCE] = source
        result[CATEGORIES] = categories
        return result
    }

    private fun fromMap(map: Map<String, Any>) {
        uid = map[UID] as String?
        idIluria = map[ID_ILURIA] as String?
        idSource = map[ID_SOURCE] as String?
        linkProduct = map[LINK_PRODUCT] as String?
        title = map[TITLE] as String?
        price = map[PRICE] as Int?
        isPublished = map[IS_PUBLISHED] as Boolean
        isPublishedSource = map[IS_PUBLISHED_SOURCE] as String
        installment = map[INSTALLMENT] as String?
        disponibility = map[DISPONIBILITY] as String?
        imageUrl = map[IMAGE_URL] as String?
        likes = map[LIKES] as MutableList<String>
        source = map[SOURCE] as String?
        categories = map[CATEGORIES] as MutableList<String>
    }

    fun getImageUrlAsUri(): Uri {
        return Uri.parse(imageUrl)
    }
}
