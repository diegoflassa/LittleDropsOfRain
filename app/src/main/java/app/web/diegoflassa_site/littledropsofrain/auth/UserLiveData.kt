package app.web.diegoflassa_site.littledropsofrain.auth

import androidx.lifecycle.MutableLiveData
import app.web.diegoflassa_site.littledropsofrain.data.entities.User


class UserLiveData : MutableLiveData<User?>() {
    init {
        value = null
    }
}