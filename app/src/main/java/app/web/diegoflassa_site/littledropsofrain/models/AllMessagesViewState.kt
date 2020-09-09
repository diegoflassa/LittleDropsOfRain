package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.fragments.AllMessagesFilters

class AllMessagesViewState : LiveData<AllMessagesViewState>(){

    var text : String = ""
    var filters: AllMessagesFilters = AllMessagesFilters.default

}
