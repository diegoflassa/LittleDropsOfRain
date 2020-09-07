package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData

class AllMessagesFilterDialogViewState : LiveData<AllMessagesFilterDialogViewState>(){

    var text : String = ""
    var selectedUserEmail : String = ""

}
