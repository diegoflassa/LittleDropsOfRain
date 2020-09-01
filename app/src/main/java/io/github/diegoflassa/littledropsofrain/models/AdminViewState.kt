package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.fragments.AllMessagesFilters

class AdminViewState : LiveData<AdminViewState>(){

    var text : String = ""
    var filters: AllMessagesFilters = AllMessagesFilters.default

}
