package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.facebook.FacebookFragment

class FacebookViewModel : ViewModel() {

    private val mViewState = FacebookViewState().apply {
        value?.text = "This is ${FacebookFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}