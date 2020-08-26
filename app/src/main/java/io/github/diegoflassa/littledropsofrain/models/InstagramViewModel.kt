package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.instagram.InstagramFragment

class InstagramViewModel : ViewModel() {

    private val mViewState = InstagramViewState().apply {
        value?.text = "This is ${InstagramFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}