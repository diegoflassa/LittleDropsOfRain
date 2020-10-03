package app.web.diegoflassa_site.littledropsofrain.models

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.data.entities.User

class SendMessageViewState : LiveData<SendMessageViewState>() {

    @Keep
    enum class SendMethod(private val method: String) {
        MESSAGE("message"),
        EMAIL("email"),
        UNKNOWN("unknown");

        override fun toString(): String {
            return method
        }
    }

    var title = ""
    var replyUid = ""
    var text = ""
    var body = ""
    var dest = User()
    var sender = User()
    var isUserAdmin = false
    var sendMethod = SendMethod.MESSAGE
}
