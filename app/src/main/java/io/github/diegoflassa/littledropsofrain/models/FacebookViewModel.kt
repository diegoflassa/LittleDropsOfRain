package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.facebook.FacebookFragment

class FacebookViewModel : ViewModel() {

    private val mViewState = FacebookViewState().apply {
        value?.text = "This is ${FacebookFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}