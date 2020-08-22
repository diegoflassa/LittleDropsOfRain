package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.send_message.SendMessageFragment

class SendMessageViewModel : ViewModel() {
    private val mViewState = SendMessageViewState().apply {
        value?.text = "This is ${SendMessageFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}