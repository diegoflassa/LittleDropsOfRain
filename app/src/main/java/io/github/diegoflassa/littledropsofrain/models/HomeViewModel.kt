package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.home.HomeFragment

class HomeViewModel : ViewModel() {

    private val mViewState = HomeViewState().apply {
        value?.text = "This is ${HomeFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}