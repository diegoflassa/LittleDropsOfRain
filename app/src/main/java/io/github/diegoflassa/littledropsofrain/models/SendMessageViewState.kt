package io.github.diegoflassa.littledropsofrain.models

import android.os.Parcelable
import androidx.lifecycle.LiveData

class SendMessageViewState : LiveData<SendMessageViewState>(){

    lateinit var text : String
    lateinit var title : String
    lateinit var body : String
    lateinit var dest : Parcelable

}
