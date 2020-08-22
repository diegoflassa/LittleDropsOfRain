package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.iluria.IluriaFragment

class IluriaViewModel : ViewModel() {

    private val mViewState = IluriaViewState().apply {
        value?.text = "This is ${IluriaFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}