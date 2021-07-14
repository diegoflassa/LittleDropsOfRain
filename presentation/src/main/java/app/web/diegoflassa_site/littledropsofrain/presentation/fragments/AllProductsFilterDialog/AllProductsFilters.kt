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

package app.web.diegoflassa_site.littledropsofrain.presentation.fragments.AllProductsFilterDialog

import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.presentation.MyApplication
import com.google.firebase.firestore.Query
import kotlinx.parcelize.Parcelize

/**
 * Object for passing filters around.
 */
@Parcelize
data class AllProductsFilters(
    var categories: MutableList<String> = ArrayList(),
    var price: Pair<Int, Int>? = null,
    var sortBy: String? = null,
    var sortDirection: Query.Direction? = null
) : Parcelable {

    fun hasCategory(): Boolean {
        return !categories.isNullOrEmpty()
    }

    fun hasPrice(): Boolean {
        return price != null
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
            desc.append(Helper.getPriceString(price!!.first))
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
        val default: AllProductsFilters
            get() {
                val filters =
                    AllProductsFilters()
                filters.categories.clear()
                filters.sortBy = Product.PRICE
                filters.sortDirection = Query.Direction.DESCENDING
                return filters
            }
    }
}
