package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.instagram.InstagramFragment

class InstagramViewModel : ViewModel() {

    private val mViewState = InstagramViewState().apply {
        value?.text = "This is ${InstagramFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}