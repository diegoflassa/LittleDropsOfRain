package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel

class UsersViewModel : ViewModel() {
    private val mViewState = UsersViewState().apply {
        value?.text = "This is ${UsersViewState::class.simpleName} Fragment"
    }
    val viewState = mViewState
}