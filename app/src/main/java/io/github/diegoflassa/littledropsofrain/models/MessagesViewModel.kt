package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.messages.MessagesFragment

class MessagesViewModel : ViewModel() {

    private val mViewState = MessagesViewState().apply {
        value?.text = "This is ${MessagesFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}