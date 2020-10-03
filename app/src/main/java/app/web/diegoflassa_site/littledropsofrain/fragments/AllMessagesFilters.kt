package app.web.diegoflassa_site.littledropsofrain.fragments

import android.content.Context
import android.text.TextUtils
import app.web.diegoflassa_site.littledropsofrain.MyApplication
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.entities.Message
import app.web.diegoflassa_site.littledropsofrain.data.entities.MessageType
import com.google.firebase.firestore.Query

/**
 * Object for passing filters around.
 */
class AllMessagesFilters {
    var read: Boolean? = null
    var type: MessageType? = null
    var emailSender: String? = null
    var sortBy: String? = null
    var sortDirection: Query.Direction? = null

    fun hasEMailSender(): Boolean {
        return !emailSender.isNullOrEmpty()
    }

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
        }
        if (hasEMailSender()) {
            if (desc.count() > 0) {
                desc.append(MyApplication.getContext().getString(R.string.and_filter))
            }
            desc.append("<b>")
            desc.append(
                MyApplication.getContext()
                    .getString(R.string.email_sender)
            )
            desc.append("</b>")
        }
        if (desc.count() == 0) {
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
            Message.EMAIL_SENDER -> {
                context.getString(R.string.sorted_by_email_sender)
            }
            else -> {
                context.getString(R.string.sorted_by_creation_date)
            }
        }
    }

    companion object {
        val default: AllMessagesFilters
            get() {
                val filters =
                    AllMessagesFilters()
                filters.read = null
                filters.type = MessageType.UNKNOWN
                filters.emailSender = null
                filters.sortBy = Message.CREATION_DATE
                filters.sortDirection = Query.Direction.DESCENDING
                return filters
            }
    }
}