package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.send_message.SendMessageFragment

class SendMessageViewModel : ViewModel() {
    private val mViewState = SendMessageViewState().apply {
        value?.text = "This is ${SendMessageFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}