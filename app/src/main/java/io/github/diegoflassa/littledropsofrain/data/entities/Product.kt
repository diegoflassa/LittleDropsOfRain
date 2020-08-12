package io.github.diegoflassa.littledropsofrain.data.entities

import android.os.Parcelable
import com.google.firebase.firestore.Blob
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*


@Parcelize
data class Product (
    var uid : String? = null,
    var idIluria : String? = null,
    var linkProduct : String? = null,
    var title : String? = null,
    var price : String? = null,
    var installment : String? = null,
    var disponibility : String? = null,
    var imageUrl : String? = null,
    var image : @RawValue Blob? = null,
    var category : String? = null
) : Parcelable {

    companion object{
        private const val UID= "uid"
        private const val ID_ILURIA= "idIluria"
        private const val LINK_PRODUCT= "linkProduct"
        private const val TITLE= "title"
        private const val PRICE= "price"
        private const val INSTALLMENT= "installment"
        private const val DISPONIBILITY= "disponibility"
        private const val IMAGE_URL= "imageUrl"
        private const val IMAGE= "image"
        private const val CATEGORY= "category"
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
        result[IMAGE] = image
        result[CATEGORY] = category
        return result
    }

    private fun fromMap(map: Map<String, Any>){
        uid = map[UID] as String?
        idIluria = map[ID_ILURIA] as String?
        linkProduct = map[LINK_PRODUCT] as String?
        title = map[TITLE] as String?
        price = map[PRICE] as String?
        installment = map[INSTALLMENT] as String?
        disponibility = map[DISPONIBILITY] as String?
        imageUrl = map[IMAGE_URL] as String?
        image = map[IMAGE] as @RawValue Blob?
        category = map[CATEGORY] as String?
    }
}