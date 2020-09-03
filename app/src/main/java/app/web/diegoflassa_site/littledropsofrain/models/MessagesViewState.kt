package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.fragments.MyMessagesFilters

class MessagesViewState : LiveData<MessagesViewState>(){

    var text : String = ""
    var filters: MyMessagesFilters = MyMessagesFilters.default

}
