package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.user_profile.UserProfileFragment

class UserProfileViewModel : ViewModel() {
    private val mViewState = UserProfileViewState().apply {
        value?.text = "This is ${UserProfileFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}