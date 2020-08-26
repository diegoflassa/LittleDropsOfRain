package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.fragments.MessagesFilters

class AdminViewState : LiveData<AdminViewState>(){

    var text : String = ""
    var filters: MessagesFilters = MessagesFilters.default

}
