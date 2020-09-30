package app.web.diegoflassa_site.littledropsofrain.data.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.util.*


@Keep
enum class Source(private val source : String){
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
data class Product (
    var uid : String? = null,
    var idIluria : String? = null,
    var linkProduct : String? = null,
    var title : String? = null,
    var price : Int? = null,
    var installment : String? = null,
    var disponibility : String? = null,
    var imageUrl : String? = null,
    var source : String? = Source.UNKNOWN.toString(),
    var categories : MutableList<String> = ArrayList()
) : Parcelable {

    companion object{
        private const val UID= "uid"
        private const val ID_ILURIA= "idIluria"
        private const val LINK_PRODUCT= "linkProduct"
        private const val TITLE= "title"
        const val PRICE= "price"
        private const val INSTALLMENT= "installment"
        private const val DISPONIBILITY= "disponibility"
        private const val IMAGE_URL= "imageUrl"
        private const val SOURCE= "source"
        const val CATEGORIES : String= "categories"
    }

    constructor(map: Map<String, Any>) : this() {
        fromMap(map)
    }

    fun toMap(): Map<String, Any?> {
        val result: HashMap<String, Any?> = HashMap()
        result[UID] = uid
        result[ID_ILURIA] = idIluria
        result[LINK_PRODUCT] = linkProduct
        result[TITLE] = title
        result[PRICE] = price
        result[INSTALLMENT] = installment
        result[DISPONIBILITY] = disponibility
        result[IMAGE_URL] = imageUrl
        result[SOURCE] = source
        result[CATEGORIES] = categories
        return result
    }

    private fun fromMap(map: Map<String, Any>){
        uid = map[UID] as String?
        idIluria = map[ID_ILURIA] as String?
        linkProduct = map[LINK_PRODUCT] as String?
        title = map[TITLE] as String?
        price = map[PRICE] as Int?
        installment = map[INSTALLMENT] as String?
        disponibility = map[DISPONIBILITY] as String?
        imageUrl = map[IMAGE_URL] as String?
        source = map[SOURCE] as String?
        categories = map[CATEGORIES] as MutableList<String>
    }
}