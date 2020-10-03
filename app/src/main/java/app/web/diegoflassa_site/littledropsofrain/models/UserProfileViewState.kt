package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData

class UserProfileViewState : LiveData<UserProfileViewState>() {

    var text: String = ""
    var name: String = ""
    var email: String = ""

}

