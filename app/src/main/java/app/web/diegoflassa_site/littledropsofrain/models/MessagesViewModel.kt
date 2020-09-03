package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.messages.MessagesFragment

class MessagesViewModel : ViewModel() {

    private val mViewState = MessagesViewState().apply {
        value?.text = "This is ${MessagesFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}