package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.all_messages.AllMessagesFragment

class AllMessagesViewModel : ViewModel() {

    private val mViewState = AllMessagesViewState().apply {
        value?.text = "This is ${AllMessagesFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}