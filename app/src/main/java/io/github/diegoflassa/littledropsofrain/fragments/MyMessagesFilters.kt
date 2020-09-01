package io.github.diegoflassa.littledropsofrain.fragments

import android.content.Context
import android.text.TextUtils
import com.google.firebase.firestore.Query
import io.github.diegoflassa.littledropsofrain.MyApplication
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.entities.Message

/**
 * Object for passing filters around.
 */
class MyMessagesFilters {
    var read: Boolean? = null
    var sortBy: String? = null
    var sortDirection: Query.Direction? = null


    fun hasRead(): Boolean {
        return (read!=null)
    }

    fun hasSortBy(): Boolean {
        return !TextUtils.isEmpty(sortBy)
    }

    fun getSearchDescription(): String {
        val desc = StringBuilder()
        desc.append("<b>")
        desc.append(
            MyApplication.getContext()
                .getString(R.string.all))
        desc.append("</b>")

        if (read != null) {
            desc.append(MyApplication.getContext().getString(R.string.for_filter))
            desc.append("<b>")
            var yesNoRead=MyApplication.getContext().getString(R.string.no)
            if(read!=null && read!!)
                yesNoRead = MyApplication.getContext().getString(R.string.yes)
            desc.append(
                MyApplication.getContext()
                    .getString(R.string.msg_read_filter, yesNoRead))
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
                filters.sortBy = Message.CREATION_DATE
                filters.sortDirection = Query.Direction.DESCENDING
                return filters
            }
    }
}