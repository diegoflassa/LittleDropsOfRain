package app.web.diegoflassa_site.littledropsofrain.fragments

import android.content.Context
import android.text.TextUtils
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import com.google.firebase.firestore.Query

/**
 * Object for passing filters around.
 */
class ProductsFilters {
    var categories: MutableList<String> = ArrayList()
    var price: MutableList<Int>? = null
    var sortBy: String? = null
    var sortDirection: Query.Direction? = null
    fun hasCategory(): Boolean {
        return !categories.isNullOrEmpty()
    }

    fun hasPrice(): Boolean {
        return !price.isNullOrEmpty()
    }

    fun hasSortBy(): Boolean {
        return !TextUtils.isEmpty(sortBy)
    }

    fun getSearchDescription(): String {
        val desc = StringBuilder()
        if (categories.isNotEmpty()) {
            desc.append("<b>")
            desc.append(categories)
            desc.append("</b>")
        } else {
            desc.append("<b>")
            desc.append(
                MyApplication.getContext()
                    .getString(R.string.all)
            )
            desc.append("</b>")
        }
        if (price != null) {
            desc.append(MyApplication.getContext().getString(R.string.for_filter))
            desc.append("<b>")
            desc.append(Helper.getPriceString(price!![0]))
            desc.append("</b>")
        }
        return desc.toString()
    }

    fun getOrderDescription(context: Context): String {
        return when (sortBy) {
            Product.PRICE -> {
                context.getString(R.string.sorted_by_price)
            }
            Product.LIKES -> {
                context.getString(R.string.sorted_by_likes)
            }
            else -> {
                context.getString(R.string.sorted_by_categories)
            }
        }
    }

    companion object {
        val default: ProductsFilters
            get() {
                val filters =
                    ProductsFilters()
                filters.categories.clear()
                filters.sortBy = Product.PRICE
                filters.sortDirection = Query.Direction.DESCENDING
                return filters
            }
    }
}