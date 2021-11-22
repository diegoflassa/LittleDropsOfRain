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

    fun getImageUrlAsUri(): Uri {
        return Uri.parse(imageUrl)
    }
}