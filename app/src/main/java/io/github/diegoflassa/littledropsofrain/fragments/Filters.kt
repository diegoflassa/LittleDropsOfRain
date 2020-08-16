package io.github.diegoflassa.littledropsofrain.fragments

import android.content.Context
import android.text.TextUtils
import com.google.firebase.firestore.Query
import io.github.diegoflassa.littledropsofrain.MyApplication
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import io.github.diegoflassa.littledropsofrain.helpers.Helper

/**
 * Object for passing filters around.
 */
class Filters {
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
        }else{
            desc.append("<b>")
            desc.append(
                MyApplication.getContext()
                    .getString(R.string.all))
            desc.append("</b>")
        }
        if (price != null) {
            desc.append(" for ")
            desc.append("<b>")
            desc.append(Helper.getPriceString(price!!))
            desc.append("</b>")
        }
        return desc.toString()
    }

    fun getOrderDescription(context: Context): String {
        return if (Product.PRICE == sortBy) {
            context.getString(R.string.sorted_by_price)
        }else{
            context.getString(R.string.sorted_by_categories)
        }
    }

    companion object {
        val default: Filters
            get() {
                val filters =
                    Filters()
                filters.categories.clear()
                filters.sortBy = Product.PRICE
                filters.sortDirection = Query.Direction.DESCENDING
                return filters
            }
    }
}