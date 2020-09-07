package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.fragments.AllMessagesFilterDialogFragment

class AllMessagesFilterDialogViewModel : ViewModel() {

    private val mViewState = AllMessagesFilterDialogViewState().apply {
        value?.text = "This is ${AllMessagesFilterDialogFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}