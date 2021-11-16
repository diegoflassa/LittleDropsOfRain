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

package app.web.diegoflassa_site.littledropsofrain.presentation.old.fragments.myMessagesFilterDialog

import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.MessageType
import app.web.diegoflassa_site.littledropsofrain.presentation.MyApplication
import com.google.firebase.firestore.Query
import kotlinx.parcelize.Parcelize

/**
 * Object for passing filters around.
 */
@ExperimentalStdlibApi
@Parcelize
data class MyMessagesFilters(
    var read: Boolean? = null,
    var type: MessageType? = null,
    var sortBy: String? = null,
    var sortDirection: Query.Direction? = null
) : Parcelable {

    fun hasRead(): Boolean {
        return (read != null)
    }

    fun hasMessageType(): Boolean {
        return ((type != null) && (type != MessageType.UNKNOWN))
    }

    fun hasSortBy(): Boolean {
        return !TextUtils.isEmpty(sortBy)
    }

    fun getSearchDescription(): String {
        val desc = StringBuilder()
        if (hasMessageType()) {
            desc.append("<b>")
            desc.append(type.toString())
            desc.append("</b>")
        } else {
            desc.append("<b>")
            desc.append(
                MyApplication.getContext()
                    .getString(R.string.all)
            )
            desc.append("</b>")
        }

        if (hasRead()) {
            desc.append(MyApplication.getContext().getString(R.string.for_filter))
            desc.append("<b>")
            var yesNoRead = MyApplication.getContext().getString(R.string.no)
            if (read != null && read!!)
                yesNoRead = MyApplication.getContext().getString(R.string.yes)
            desc.append(
                MyApplication.getContext()
                    .getString(R.string.msg_read_filter, yesNoRead)
            )
            desc.append("</b>")
        }

        return desc.toString()
    }

    fun getOrderDescription(context: Context): String {
        return when (sortBy) {
            Message.READ -> {
                context.getString(R.string.sorted_by_read)
            }
            Message.CREATION_DATE -> {
                context.getString(R.string.sorted_by_creation_date)
            }
            else -> {
                context.getString(R.string.sorted_by_creation_date)
            }
        }
    }

    companion object {
        val default: MyMessagesFilters
            get() {
                val filters =
                    MyMessagesFilters()
                filters.read = null
                filters.type = MessageType.UNKNOWN
                filters.sortBy = Message.CREATION_DATE
                filters.sortDirection = Query.Direction.DESCENDING
                return filters
            }
    }
}
