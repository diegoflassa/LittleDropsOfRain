package io.github.diegoflassa.littledropsofrain.models

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.entities.User

class SendMessageViewState : LiveData<SendMessageViewState>(){

    @Keep
    enum class SendMethod(private val method : String){
        MESSAGE("message"),
        EMAIL("email"),
        UNKNOWN("Unknown");

        override fun toString(): String {
            return method
        }
    }

    var text = ""
    var title = ""
    var body = ""
    var dest = User()
    var sender = User()
    var isUserAdmin = false
    var sendMethod = SendMethod.UNKNOWN
}
