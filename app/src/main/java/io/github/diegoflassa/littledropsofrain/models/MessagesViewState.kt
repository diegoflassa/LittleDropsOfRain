package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.fragments.MyMessagesFilters

class MessagesViewState : LiveData<MessagesViewState>(){

    var text : String = ""
    var filters: MyMessagesFilters = MyMessagesFilters.default

}
