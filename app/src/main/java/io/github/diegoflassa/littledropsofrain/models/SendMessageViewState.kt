package io.github.diegoflassa.littledropsofrain.models

import android.os.Parcelable
import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.entities.User

class SendMessageViewState : LiveData<SendMessageViewState>(){

    var text : String = ""
    var title : String = ""
    var body : String = ""
    var dest : Parcelable = User()

}
